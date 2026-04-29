import { useMemo, useState } from 'react';

import SectionHeading from '../components/SectionHeading.jsx';
import {
  useCreateSkillMutation,
  useDeleteSkillMutation,
  useGetSkillsQuery,
  useUpdateSkillMutation,
} from '../features/platform/platformApi.js';
import { getSkillKey, normalizeSkillName, suggestSkillCategory } from '../utils/skills.js';

const initialFormData = {
  name: '',
  category: 'Mentorship',
  description: '',
};

function AdminSkillsPage() {
  const { data: skillsResponse, isLoading, isError } = useGetSkillsQuery();
  const [createSkill, { isLoading: isCreating }] = useCreateSkillMutation();
  const [updateSkill, { isLoading: isUpdating }] = useUpdateSkillMutation();
  const [deleteSkill, { isLoading: isDeleting }] = useDeleteSkillMutation();
  const [formData, setFormData] = useState(initialFormData);
  const [editingSkillId, setEditingSkillId] = useState(null);
  const [message, setMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const skills = useMemo(() => skillsResponse?.content || [], [skillsResponse]);
  const isSubmitting = isCreating || isUpdating;
  const existingSkillKeys = useMemo(
    () => new Map(skills.map((skill) => [getSkillKey(skill.name), skill])),
    [skills]
  );

  const resetForm = () => {
    setFormData(initialFormData);
    setEditingSkillId(null);
  };

  const handleFieldChange = (field, value) => {
    setFormData((current) => {
      const nextForm = {
        ...current,
        [field]: value,
      };

      if (field === 'name') {
        const suggestedCategory = suggestSkillCategory(value);

        if (!current.category.trim() || current.category === 'Mentorship' || current.category === suggestSkillCategory(current.name)) {
          nextForm.category = suggestedCategory;
        }
      }

      return nextForm;
    });
  };

  const handleSubmit = async () => {
    const normalizedName = normalizeSkillName(formData.name);
    const normalizedCategory = formData.category.trim() || suggestSkillCategory(normalizedName);
    const normalizedDescription = formData.description.trim();

    if (!normalizedName || !normalizedCategory) {
      setErrorMessage('Skill name and category are required.');
      return;
    }

    const existingSkill = existingSkillKeys.get(getSkillKey(normalizedName));

    if (existingSkill && existingSkill.id !== editingSkillId) {
      setErrorMessage(`"${normalizedName}" already exists in the skill catalog.`);
      return;
    }

    try {
      setErrorMessage('');
      setMessage('');

      const payload = {
        name: normalizedName,
        category: normalizedCategory,
        description: normalizedDescription,
      };

      if (editingSkillId) {
        await updateSkill({ skillId: editingSkillId, payload }).unwrap();
        setMessage('Skill updated successfully.');
      } else {
        await createSkill(payload).unwrap();
        setMessage('Skill created successfully.');
      }

      resetForm();
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Unable to save skill.');
    }
  };

  const handleEdit = (skill) => {
    setMessage('');
    setErrorMessage('');
    setEditingSkillId(skill.id);
    setFormData({
      name: skill.name || '',
      category: skill.category || 'Mentorship',
      description: skill.description || '',
    });
  };

  const handleDelete = async (skill) => {
    const shouldDelete = window.confirm(`Delete "${skill.name}" from the platform skill catalog?`);

    if (!shouldDelete) {
      return;
    }

    try {
      setMessage('');
      setErrorMessage('');
      await deleteSkill(skill.id).unwrap();
      setMessage('Skill deleted successfully.');

      if (editingSkillId === skill.id) {
        resetForm();
      }
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Unable to delete skill.');
    }
  };

  return (
    <div className="page-stack">
      <SectionHeading
        title="⭐ Skill Catalog"
        subtitle="Manage the central skills available across mentors, filters, and platform matching."
      />

      <section className="card dashboard-panel">
        <SectionHeading
          title={editingSkillId ? '✏️ Edit Skill' : '➕ Add New Skill'}
          subtitle="Admins can add, refine, or clean up the platform skill catalog at any time."
        />
        <div className="booking-section">
          <h4>🏷️ Skill Name</h4>
          <input
            value={formData.name}
            onChange={(event) => handleFieldChange('name', event.target.value)}
            placeholder="Example: Spring Boot"
          />
        </div>
        <div className="booking-section">
          <h4>📚 Category</h4>
          <input
            value={formData.category}
            onChange={(event) => handleFieldChange('category', event.target.value)}
            placeholder="Backend, Frontend, Data / AI..."
          />
        </div>
        <div className="booking-section">
          <h4>📝 Description</h4>
          <input
            value={formData.description}
            onChange={(event) => handleFieldChange('description', event.target.value)}
            placeholder="Short platform-facing description"
          />
        </div>
        {message ? <p className="success-text">{message}</p> : null}
        {errorMessage ? <p className="muted-text">{errorMessage}</p> : null}
        <div className="button-row">
          <button className="primary-button" onClick={handleSubmit} type="button" disabled={isSubmitting}>
            {isSubmitting ? 'Saving...' : editingSkillId ? '✅ Update Skill' : '➕ Add Skill'}
          </button>
          {editingSkillId ? (
            <button className="ghost-button" onClick={resetForm} type="button" disabled={isSubmitting}>
              Cancel Edit
            </button>
          ) : null}
        </div>
      </section>

      <section className="card dashboard-panel">
        <SectionHeading
          title="⭐ Available Skills"
          subtitle="Current skill-service catalog with admin controls."
        />
        {isLoading ? <div>Loading skills...</div> : null}
        {isError ? <div>Unable to load skills right now.</div> : null}
        {!isLoading && !isError ? (
          <div className="detail-list">
            {skills.map((skill) => (
              <article className="detail-item" key={skill.id}>
                <div>
                  <h3>{skill.name}</h3>
                  <p>{skill.description || 'No description provided.'}</p>
                </div>
                <div className="detail-meta">
                  <strong>{skill.category}</strong>
          chrome
                  <div className="button-row">
                    <button className="secondary-button" onClick={() => handleEdit(skill)} type="button" disabled={isDeleting}>
                      Edit
                    </button>
                    <button className="ghost-button" onClick={() => handleDelete(skill)} type="button" disabled={isDeleting}>
                      🗑️ Delete
                    </button>
                  </div>
                </div>
              </article>
            ))}
            {!skills.length ? <div className="activity-item">No skills found in the skill-service yet.</div> : null}
          </div>
        ) : null}
      </section>
    </div>
  );
}

export default AdminSkillsPage;
