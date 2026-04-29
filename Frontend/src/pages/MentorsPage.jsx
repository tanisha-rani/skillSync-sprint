import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import SectionHeading from '../components/SectionHeading.jsx';
import MentorCard from '../components/MentorCard.jsx';
import { useGetMentorsQuery, useGetSkillsQuery, useGetUsersQuery } from '../features/platform/platformApi.js';
import { buildUserMap, mapMentorToCard } from '../utils/viewMappers.js';

function MentorsPage() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedSkill, setSelectedSkill] = useState('');
  const [selectedMentor, setSelectedMentor] = useState(null);
  const { data: mentorsResponse, isLoading: isMentorsLoading, isError: isMentorsError } = useGetMentorsQuery();
  const { data: usersResponse } = useGetUsersQuery();
  const { data: skillsResponse } = useGetSkillsQuery();

  const userMap = useMemo(() => buildUserMap(usersResponse?.content || []), [usersResponse]);
  const skills = useMemo(() => (skillsResponse?.content || []).slice(0, 6).map((item) => item.name), [skillsResponse]);
  const mentors = useMemo(
    () => (mentorsResponse?.content || []).map((mentor) => mapMentorToCard(mentor, userMap)),
    [mentorsResponse, userMap]
  );

  const filteredMentors = useMemo(
    () =>
      mentors.filter((mentor) => {
        const normalizedSearch = searchTerm.toLowerCase();
        const matchesSearch =
          !normalizedSearch ||
          mentor.name.toLowerCase().includes(normalizedSearch) ||
          mentor.bio.toLowerCase().includes(normalizedSearch) ||
          mentor.skills.some((skill) => skill.toLowerCase().includes(normalizedSearch));

        const matchesSkill = !selectedSkill || mentor.skills.includes(selectedSkill);

        return matchesSearch && matchesSkill;
      }),
    [mentors, searchTerm, selectedSkill]
  );

  return (
    <div className="page-stack">
      <SectionHeading
        title="👨‍🏫 Find a Mentor"
        subtitle="Discover expert mentors matched to your learning goals."
        action={
          <button className="primary-button" onClick={() => setSelectedSkill('')} type="button">
            ↺ Reset Filters
          </button>
        }
      />

      <section className="card search-panel">
        <input
          placeholder="Search mentors by name, bio, or skill..."
          type="text"
          value={searchTerm}
          onChange={(event) => setSearchTerm(event.target.value)}
        />
        <div className="chip-row">
          <button
            className={!selectedSkill ? 'filter-chip role-preview active' : 'filter-chip'}
            onClick={() => setSelectedSkill('')}
            type="button"
          >
            All Skills
          </button>
          {skills.map((skill) => (
            <button
              className={selectedSkill === skill ? 'filter-chip role-preview active' : 'filter-chip'}
              key={skill}
              onClick={() => setSelectedSkill(skill)}
              type="button"
            >
              {skill}
            </button>
          ))}
        </div>
        <p className="muted-text">Showing {filteredMentors.length} mentors matching your criteria</p>
      </section>

      {isMentorsLoading ? <div className="card">Loading mentors...</div> : null}
      {isMentorsError ? <div className="card">Unable to load mentors right now.</div> : null}

      {!isMentorsLoading && !isMentorsError ? (
        <div className="mentor-grid">
          {filteredMentors.map((mentor) => (
            <MentorCard
              key={mentor.id}
              mentor={mentor}
              onPrimaryAction={() => navigate(`/booking?mentorId=${mentor.id}`)}
              onSecondaryAction={() => setSelectedMentor(mentor)}
            />
          ))}
        </div>
      ) : null}

      {selectedMentor ? (
        <div className="modal-backdrop" role="presentation">
          <div className="modal-card mentor-detail-modal" role="dialog" aria-modal="true" aria-labelledby="mentor-detail-title">
            <div className="mentor-header">
              <div className="avatar-badge warm">{selectedMentor.initials}</div>
              <div>
                <p className="eyebrow">👨‍🏫 Mentor Profile</p>
                <h3 id="mentor-detail-title">{selectedMentor.name}</h3>
                <p>{selectedMentor.experience}</p>
              </div>
              <span className="status-pill">{selectedMentor.status}</span>
            </div>

            <p className="mentor-rating">
              {'★'.repeat(5)} <span>{selectedMentor.rating}</span> ({selectedMentor.reviews} reviews)
            </p>

            <div className="summary-list">
              <div><span>Rate</span><strong>{selectedMentor.price}</strong></div>
              <div><span>Status</span><strong>{selectedMentor.raw?.status || selectedMentor.status}</strong></div>
              <div><span>Availability</span><strong>{selectedMentor.raw?.isAvailable ? 'Open for bookings' : 'Bookings paused'}</strong></div>
              <div><span>Total Reviews</span><strong>{selectedMentor.reviews}</strong></div>
            </div>

            <div>
              <h4>Skills</h4>
              <div className="tag-list">
                {selectedMentor.skills.map((skill) => (
                  <span className="tag" key={skill}>
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div>
              <h4>Bio</h4>
              <p className="muted-text">{selectedMentor.bio}</p>
            </div>

            <div className="button-row modal-actions">
              <button className="ghost-button" onClick={() => setSelectedMentor(null)} type="button">
                Close
              </button>
              <button
                className="primary-button"
                onClick={() => navigate(`/booking?mentorId=${selectedMentor.id}`)}
                type="button"
              >
                📅 Book {selectedMentor.name}
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default MentorsPage;
