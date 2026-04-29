import { useMemo, useState } from 'react';

import SectionHeading from '../components/SectionHeading.jsx';
import StatCard from '../components/StatCard.jsx';
import {
  useApproveMentorMutation,
  useCreateSkillMutation,
  useGetAdminDashboardQuery,
  useGetMentorsQuery,
  useGetMentorsByStatusQuery,
  useGetSessionsQuery,
  useGetSkillsQuery,
  useGetUsersQuery,
  useRejectMentorMutation,
} from '../features/platform/platformApi.js';
import { getSkillKey, normalizeSkillName, suggestSkillCategory } from '../utils/skills.js';
import { buildUserMap } from '../utils/viewMappers.js';

function AdminPage() {
  const { data: dashboardData, isLoading: isDashboardLoading, isError: isDashboardError } = useGetAdminDashboardQuery();
  const { data: pendingMentorsResponse } = useGetMentorsByStatusQuery({ status: 'PENDING' });
  const { data: allMentorsResponse } = useGetMentorsQuery();
  const { data: sessionsResponse } = useGetSessionsQuery();
  const { data: skillsResponse } = useGetSkillsQuery();
  const { data: usersResponse } = useGetUsersQuery();
  const [approveMentor, { isLoading: isApproving }] = useApproveMentorMutation();
  const [rejectMentor, { isLoading: isRejecting }] = useRejectMentorMutation();
  const [createSkill] = useCreateSkillMutation();
  const [mentorApprovalPrompt, setMentorApprovalPrompt] = useState(null);
  const [mentorRejectPrompt, setMentorRejectPrompt] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [selectedStat, setSelectedStat] = useState(null);

  const userMap = useMemo(() => buildUserMap(usersResponse?.content || []), [usersResponse]);
  const allUsers = useMemo(() => usersResponse?.content || [], [usersResponse]);
  const allMentors = useMemo(() => allMentorsResponse?.content || [], [allMentorsResponse]);
  const approvedMentors = allMentors.filter((mentor) => mentor.status === 'APPROVED');
  const allSessions = useMemo(() => sessionsResponse?.content || [], [sessionsResponse]);
  const mentorById = useMemo(
    () => new Map(allMentors.map((mentor) => [String(mentor.id), mentor])),
    [allMentors]
  );
  const pendingMentors = [
    ...((pendingMentorsResponse?.content || []).filter((mentor) => mentor.status === 'PENDING')),
    ...(allMentors.filter((mentor) => mentor.status === 'PENDING')),
  ].filter((mentor, index, array) => array.findIndex((item) => item.id === mentor.id) === index);
  const mentorAvailability = approvedMentors.slice(0, 6);
  const existingSkillNames = new Set((skillsResponse?.content || []).map((skill) => getSkillKey(skill.name)));
  const usersStats = typeof dashboardData?.users === 'object' ? dashboardData.users : null;
  const sessionsStats = typeof dashboardData?.sessions === 'object' ? dashboardData.sessions : null;
  const mentorsStats = typeof dashboardData?.mentors === 'object' ? dashboardData.mentors : null;
  const dashboardWarnings = [
    typeof dashboardData?.users === 'string' ? `Users service: ${dashboardData.users}` : null,
    typeof dashboardData?.sessions === 'string' ? `Sessions service: ${dashboardData.sessions}` : null,
    typeof dashboardData?.mentors === 'string' ? `Mentors service: ${dashboardData.mentors}` : null,
  ].filter(Boolean);

  const stats = useMemo(() => {
    const totalUsers = usersStats?.totalUsers || 0;
    const totalMentors = mentorsStats?.totalMentors || 0;
    const totalSessions = sessionsStats?.totalSessions || 0;

    return [
      { label: 'Total Users', value: String(totalUsers), change: 'Live count from user service', icon: 'users' },
      { label: 'Approved Mentors', value: String(totalMentors), change: 'Approved mentors from mentor service', icon: 'mentor' },
      { label: 'Sessions Booked', value: String(totalSessions), change: 'Live count from session service', icon: 'calendar' },
      {
        label: 'Pending Approvals',
        value: String(pendingMentors.length),
        change: 'Mentors waiting for review',
        icon: 'shield',
      },
    ];
  }, [mentorsStats?.totalMentors, pendingMentors.length, sessionsStats?.totalSessions, usersStats?.totalUsers]);

  const activity = [
    `${pendingMentors.length} mentor applications are pending approval.`,
    `${sessionsStats?.totalSessions || 0} total sessions have been booked.`,
    `${usersStats?.totalUsers || 0} users are currently registered.`,
  ];

  const performance = [
    {
      label: 'Mentor Coverage',
      value:
        usersStats?.totalUsers && mentorsStats?.totalMentors
          ? `${Math.min(100, Math.round((mentorsStats.totalMentors / usersStats.totalUsers) * 100))}%`
          : '0%',
      color: 'var(--accent)',
    },
    {
      label: 'Session Density',
      value:
        usersStats?.totalUsers && sessionsStats?.totalSessions
          ? `${Math.min(100, Math.round((sessionsStats.totalSessions / usersStats.totalUsers) * 100))}%`
          : '0%',
      color: 'var(--success)',
    },
    {
      label: 'Approval Queue',
      value: `${Math.min(100, pendingMentors.length * 10)}%`,
      color: 'var(--orange)',
    },
  ];

  const syncApprovedMentorSkills = async (mentor) => {
    if (!mentor?.skills?.length) {
      return;
    }

    const missingSkills = mentor.skills
      .map((skill) => normalizeSkillName(skill))
      .filter((skill, index, array) => skill && array.indexOf(skill) === index)
      .filter((skill) => !existingSkillNames.has(getSkillKey(skill)));

    await Promise.all(
      missingSkills.map((skill) =>
        createSkill({
          name: skill,
          category: suggestSkillCategory(skill),
          description: `Auto-added when mentor ${mentor.applicantName || mentor.userId} was approved.`,
        }).unwrap()
      )
    );
  };

  const handleApprove = async (mentorId) => {
    const mentor = pendingMentors.find((item) => item.id === mentorId);

    if (!mentor) {
      return;
    }

    await approveMentor(mentorId).unwrap();

    if (!mentor?.skills?.length) {
      return;
    }

    setMentorApprovalPrompt(mentor);
  };

  const handleReject = async (mentorId) => {
    const mentor = pendingMentors.find((item) => item.id === mentorId);

    if (!mentor) {
      return;
    }

    setMentorRejectPrompt(mentor);
    setRejectionReason('');
  };

  const handleConfirmSkillSync = async () => {
    if (!mentorApprovalPrompt) {
      return;
    }

    await syncApprovedMentorSkills(mentorApprovalPrompt);
    setMentorApprovalPrompt(null);
  };

  const handleSkipSkillSync = () => {
    setMentorApprovalPrompt(null);
  };

  const handleConfirmReject = async () => {
    if (!mentorRejectPrompt) {
      return;
    }

    await rejectMentor({
      mentorId: mentorRejectPrompt.id,
      reason: rejectionReason.trim(),
    }).unwrap();
    setMentorRejectPrompt(null);
    setRejectionReason('');
  };

  const renderSelectedStatDetails = () => {
    if (!selectedStat) {
      return null;
    }

    const renderEmptyState = (message) => <div className="activity-item">{message}</div>;

    return (
      <section className="card dashboard-panel">
        <SectionHeading
          title={selectedStat}
          subtitle="Detailed records for the selected admin metric."
        />
        <div className="detail-list">
          {selectedStat === 'Total Users' &&
            (allUsers.length
              ? allUsers.map((user) => (
                  <article className="detail-item" key={user.id}>
                    <div>
                      <h3>{user.name}</h3>
                      <p>{user.email}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{user.role}</strong>
                      <span>{user.isActive ? 'Active' : 'Inactive'}</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('No users found.'))}

          {selectedStat === 'Approved Mentors' &&
            (approvedMentors.length
              ? approvedMentors.map((mentor) => {
                  const user = userMap.get(String(mentor.userId));

                  return (
                    <article className="detail-item" key={mentor.id}>
                      <div>
                        <h3>{mentor.applicantName || user?.name || `User #${mentor.userId}`}</h3>
                        <p>{(mentor.skills || []).join(', ') || 'No skills added'}</p>
                      </div>
                      <div className="detail-meta">
                        <strong>{mentor.isAvailable ? 'Open for bookings' : 'Bookings paused'}</strong>
                        <span>{mentor.experienceYears} yrs</span>
                      </div>
                    </article>
                  );
                })
              : renderEmptyState('No approved mentors found.'))}

          {selectedStat === 'Sessions Booked' &&
            (allSessions.length
              ? allSessions.map((session) => {
                  const mentor = mentorById.get(String(session.mentorId));
                  const mentorUser = mentor ? userMap.get(String(mentor.userId)) : null;
                  const learner = userMap.get(String(session.learnerId));

                  return (
                    <article className="detail-item" key={session.id}>
                      <div>
                        <h3>{session.topic}</h3>
                        <p>
                          {learner?.name || `Learner #${session.learnerId}`} with{' '}
                          {session.mentorName || mentor?.applicantName || mentorUser?.name || 'Mentor'}
                        </p>
                      </div>
                      <div className="detail-meta">
                        <strong>{session.status}</strong>
                        <span>{session.sessionDate || 'Date pending'}</span>
                      </div>
                    </article>
                  );
                })
              : renderEmptyState('No sessions found.'))}

          {selectedStat === 'Pending Approvals' &&
            (pendingMentors.length
              ? pendingMentors.map((mentor) => {
                  const user = userMap.get(String(mentor.userId));

                  return (
                    <article className="detail-item" key={mentor.id}>
                      <div>
                        <h3>{mentor.applicantName || user?.name || `User #${mentor.userId}`}</h3>
                        <p>{mentor.applicantEmail || user?.email || 'Email unavailable'}</p>
                      </div>
                      <div className="detail-meta">
                        <strong>PENDING</strong>
                        <span>{mentor.experienceYears} yrs</span>
                      </div>
                    </article>
                  );
                })
              : renderEmptyState('No pending mentor approvals right now.'))}
        </div>
      </section>
    );
  };

  return (
    <div className="page-stack">
      <SectionHeading
        title="🛡️ Admin Console"
        subtitle="Live platform overview with mentor approvals and core service metrics."
      />

      {isDashboardLoading ? <div className="card">Loading admin dashboard...</div> : null}
      {isDashboardError ? <div className="card">Unable to load admin dashboard right now.</div> : null}

      {!isDashboardLoading && !isDashboardError ? (
        <>
          <section className="stats-grid">
            {stats.map((item) => (
              <StatCard
                isActive={selectedStat === item.label}
                item={item}
                key={item.label}
                onClick={() => setSelectedStat((current) => (current === item.label ? null : item.label))}
              />
            ))}
          </section>

          {renderSelectedStatDetails()}

          {dashboardWarnings.length ? (
            <section className="card dashboard-panel">
              <SectionHeading
                title="⚠️ Service Warnings"
                subtitle="These counts are live. If one service fails, it shows up here instead of silently becoming zero."
              />
              <div className="detail-list">
                {dashboardWarnings.map((warning) => (
                  <div className="activity-item" key={warning}>{warning}</div>
                ))}
              </div>
            </section>
          ) : null}

          <div className="admin-layout">
            <section className="card admin-panel">
              <div className="panel-title-row">
                <h3>Pending Mentor Approvals</h3>
                <button className="link-button" type="button">
                  {pendingMentors.length} pending
                </button>
              </div>

              <div className="approval-list">
                {pendingMentors.map((mentor) => {
                  const user = userMap.get(String(mentor.userId));
                  const displayName = mentor.applicantName || user?.name || `User #${mentor.userId}`;
                  const displayEmail = mentor.applicantEmail || user?.email || 'Email unavailable';

                  return (
                    <div className="approval-row" key={mentor.id}>
                      <div>
                        <strong>{displayName}</strong>
                        <p>{displayEmail}</p>
                      </div>
                      <div className="tag-list">
                        {(mentor.skills || []).map((skill) => (
                          <span className="tag" key={skill}>
                            {skill}
                          </span>
                        ))}
                      </div>
                      <span>{mentor.experienceYears} yrs</span>
                      <div className="button-row">
                        <button
                          className="success-button"
                          onClick={() => handleApprove(mentor.id)}
                          type="button"
                          disabled={isApproving || isRejecting}
                        >
                          Approve
                        </button>
                        <button
                          className="secondary-button"
                          onClick={() => handleReject(mentor.id)}
                          type="button"
                          disabled={isApproving || isRejecting}
                        >
                          Reject
                        </button>
                      </div>
                    </div>
                  );
                })}
                {!pendingMentors.length ? <div className="activity-item">No pending mentor approvals right now.</div> : null}
              </div>
            </section>

            <aside className="card activity-card">
              <h3>Recent Activity</h3>
              <div className="activity-list">
                {activity.map((item) => (
                  <div className="activity-item" key={item}>
                    {item}
                  </div>
                ))}
              </div>
            </aside>
          </div>

          <section className="card performance-card">
            <h3>Platform Performance</h3>
            {performance.map((item) => (
              <div className="metric-row" key={item.label}>
                <div className="metric-label">
                  <span>{item.label}</span>
                  <strong>{item.value}</strong>
                </div>
                <div className="metric-bar">
                  <span style={{ background: item.color, width: item.value }} />
                </div>
              </div>
            ))}
          </section>

          <section className="card dashboard-panel">
            <SectionHeading
              title="👨‍🏫 Mentor Availability Overview"
              subtitle="Platform-wide mentor visibility belongs in the admin console."
            />
            <div className="detail-list">
              {mentorAvailability.map((mentor) => {
                const user = userMap.get(String(mentor.userId));
                const displayName = mentor.applicantName || user?.name || 'Mentor Applicant';

                return (
                  <article className="detail-item" key={mentor.id}>
                    <div>
                      <h3>{displayName}</h3>
                      <p>{(mentor.skills || []).join(', ') || 'No skills added'}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{mentor.isAvailable ? 'Open for bookings' : 'Bookings paused'}</strong>
                      <span>{mentor.status}</span>
                    </div>
                  </article>
                );
              })}
              {!mentorAvailability.length ? <div className="activity-item">Approved mentor availability will appear here once mentors are active.</div> : null}
            </div>
          </section>

          {mentorApprovalPrompt ? (
            <div className="modal-backdrop" role="presentation">
              <div className="modal-card" role="dialog" aria-modal="true" aria-labelledby="skill-sync-title">
                <p className="eyebrow">✅ Mentor Approved</p>
                <h3 id="skill-sync-title">Add mentor skills to the platform catalog?</h3>
                <p className="muted-text">
                  {mentorApprovalPrompt.applicantName || `User #${mentorApprovalPrompt.userId}`} is approved now. You can also
                  save their listed skills into the shared skill-service so filters and future mentors reuse them cleanly.
                </p>
                <div className="tag-list">
                  {(mentorApprovalPrompt.skills || []).map((skill) => (
                    <span className="tag" key={skill}>
                      {normalizeSkillName(skill)}
                    </span>
                  ))}
                </div>
                <div className="button-row modal-actions">
                  <button className="primary-button" onClick={handleConfirmSkillSync} type="button">
                    Add Skills
                  </button>
                  <button className="ghost-button" onClick={handleSkipSkillSync} type="button">
                    Skip for Now
                  </button>
                </div>
              </div>
            </div>
          ) : null}

          {mentorRejectPrompt ? (
            <div className="modal-backdrop" role="presentation">
              <div className="modal-card" role="dialog" aria-modal="true" aria-labelledby="reject-title">
                <p className="eyebrow">🚫 Reject Application</p>
                <h3 id="reject-title">Share a clear reason with the mentor</h3>
                <p className="muted-text">
                  This message will be shown on the mentor dashboard so they know what to improve before reapplying.
                </p>
                <div className="booking-section">
                  <h4>Rejection Reason</h4>
                  <input
                    value={rejectionReason}
                    onChange={(event) => setRejectionReason(event.target.value)}
                    placeholder="Example: Please add more project experience and expand your bio."
                  />
                </div>
                <div className="button-row modal-actions">
                  <button className="secondary-button" onClick={handleConfirmReject} type="button">
                    Reject Mentor
                  </button>
                  <button
                    className="ghost-button"
                    onClick={() => {
                      setMentorRejectPrompt(null);
                      setRejectionReason('');
                    }}
                    type="button"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          ) : null}
        </>
      ) : null}
    </div>
  );
}

export default AdminPage;
