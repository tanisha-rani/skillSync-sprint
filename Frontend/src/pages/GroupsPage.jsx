import { useMemo, useState } from 'react';

import SectionHeading from '../components/SectionHeading.jsx';
import GroupCard from '../components/GroupCard.jsx';
import {
  useCreateGroupMutation,
  useGetGroupsQuery,
  useGetMyGroupsQuery,
  useGetUsersQuery,
  useJoinGroupMutation,
  useLeaveGroupMutation,
} from '../features/platform/platformApi.js';
import { buildUserMap, mapGroupToCard } from '../utils/viewMappers.js';

function GroupsPage({ currentUser }) {
  const [activeTab, setActiveTab] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [createMessage, setCreateMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    topics: '',
    maxMembers: 50,
  });

  const { data: usersResponse } = useGetUsersQuery();
  const { data: groupsResponse, isLoading: isGroupsLoading, isError: isGroupsError } = useGetGroupsQuery();
  const { data: myGroupsResponse } = useGetMyGroupsQuery(
    { userId: currentUser.userId },
    { skip: !currentUser?.userId }
  );
  const [joinGroup, { isLoading: isJoining }] = useJoinGroupMutation();
  const [leaveGroup, { isLoading: isLeaving }] = useLeaveGroupMutation();
  const [createGroup, { isLoading: isCreating }] = useCreateGroupMutation();

  const userMap = useMemo(() => buildUserMap(usersResponse?.content || []), [usersResponse]);
  const sourceGroups = useMemo(
    () => (activeTab === 'my' ? myGroupsResponse?.content || [] : groupsResponse?.content || []),
    [activeTab, groupsResponse, myGroupsResponse]
  );

  const groups = useMemo(
    () => sourceGroups.map((group) => mapGroupToCard(group, userMap, currentUser.userId)),
    [currentUser.userId, sourceGroups, userMap]
  );

  const filteredGroups = useMemo(
    () =>
      groups.filter((group) => {
        const normalizedSearch = searchTerm.toLowerCase();
        return (
          !normalizedSearch ||
          group.name.toLowerCase().includes(normalizedSearch) ||
          group.description.toLowerCase().includes(normalizedSearch) ||
          group.tags.some((tag) => tag.toLowerCase().includes(normalizedSearch))
        );
      }),
    [groups, searchTerm]
  );

  const handleJoin = async (group) => {
    try {
      setErrorMessage('');
      await joinGroup({ groupId: group.id, userId: currentUser.userId }).unwrap();
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Unable to join group.');
    }
  };

  const handleLeave = async (group) => {
    try {
      setErrorMessage('');
      await leaveGroup({ groupId: group.id, userId: currentUser.userId }).unwrap();
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Unable to leave group.');
    }
  };

  const handleCreate = async () => {
    if (!formData.name.trim() || !formData.topics.trim()) {
      setErrorMessage('Group name and at least one topic are required.');
      return;
    }

    try {
      setErrorMessage('');
      setCreateMessage('');

      await createGroup({
        name: formData.name.trim(),
        description: formData.description.trim(),
        topics: formData.topics.split(',').map((item) => item.trim()).filter(Boolean),
        creatorUserId: Number(currentUser.userId),
        maxMembers: Number(formData.maxMembers),
      }).unwrap();

      setCreateMessage('Group created successfully.');
      setShowCreateForm(false);
      setFormData({
        name: '',
        description: '',
        topics: '',
        maxMembers: 50,
      });
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Unable to create group.');
    }
  };

  return (
    <div className="page-stack">
      <SectionHeading
        title="👥 Learning Groups"
        subtitle="Join peer communities and learn together."
        action={
          <button className="primary-button" onClick={() => setShowCreateForm((current) => !current)} type="button">
            {showCreateForm ? '✕ Close Form' : '➕ Create New Group'}
          </button>
        }
      />

      {showCreateForm ? (
        <section className="card search-panel">
          <div className="booking-section">
            <h4>👥 Group Name</h4>
            <input value={formData.name} onChange={(event) => setFormData((current) => ({ ...current, name: event.target.value }))} />
          </div>
          <div className="booking-section">
            <h4>📝 Description</h4>
            <input
              value={formData.description}
              onChange={(event) => setFormData((current) => ({ ...current, description: event.target.value }))}
            />
          </div>
          <div className="booking-section">
            <h4>🏷️ Topics</h4>
            <input
              placeholder="Java, Spring Boot, DSA"
              value={formData.topics}
              onChange={(event) => setFormData((current) => ({ ...current, topics: event.target.value }))}
            />
          </div>
          <div className="booking-section">
            <h4>👤 Max Members</h4>
            <input
              type="number"
              value={formData.maxMembers}
              onChange={(event) => setFormData((current) => ({ ...current, maxMembers: event.target.value }))}
            />
          </div>
          <button className="primary-button" onClick={handleCreate} type="button" disabled={isCreating}>
            {isCreating ? 'Creating...' : '➕ Create Group'}
          </button>
        </section>
      ) : null}

      <section className="card search-panel">
        <input
          placeholder="Search groups by topic, skill, or name..."
          type="text"
          value={searchTerm}
          onChange={(event) => setSearchTerm(event.target.value)}
        />
        <div className="tab-row">
          <button className={activeTab === 'all' ? 'tab-chip active' : 'tab-chip'} onClick={() => setActiveTab('all')} type="button">
            All Groups
          </button>
          <button className={activeTab === 'my' ? 'tab-chip active' : 'tab-chip'} onClick={() => setActiveTab('my')} type="button">
            My Groups
          </button>
        </div>
        {createMessage ? <p className="success-text">{createMessage}</p> : null}
        {errorMessage ? <p className="muted-text">{errorMessage}</p> : null}
      </section>

      {isGroupsLoading ? <div className="card">Loading groups...</div> : null}
      {isGroupsError ? <div className="card">Unable to load groups right now.</div> : null}

      {!isGroupsLoading && !isGroupsError ? (
        <div className="group-grid">
          {filteredGroups.map((group) => (
            <GroupCard
              group={group}
              isJoining={isJoining}
              isLeaving={isLeaving}
              key={group.id}
              onJoin={handleJoin}
              onLeave={handleLeave}
            />
          ))}
        </div>
      ) : null}
    </div>
  );
}

export default GroupsPage;
