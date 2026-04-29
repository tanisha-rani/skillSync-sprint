import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import SectionHeading from '../components/SectionHeading.jsx';
import StatCard from '../components/StatCard.jsx';
import MentorCard from '../components/MentorCard.jsx';
import SessionTable from '../components/SessionTable.jsx';
import {
  useApplyAsMentorMutation,
  useCompleteSessionMutation,
  useCreateReviewMutation,
  useGetMentorByUserIdQuery,
  useGetMentorsQuery,
  useGetMyGroupsQuery,
  useGetNotificationsByUserQuery,
  useGetReviewsByLearnerQuery,
  useGetSessionsByMentorQuery,
  useGetSessionsByUserQuery,
  useGetUsersQuery,
  useMarkNotificationReadMutation,
  useReapplyMentorMutation,
  useUpdateMentorMutation,
} from '../features/platform/platformApi.js';
import { buildUserMap, mapMentorToCard, mapSessionToRow } from '../utils/viewMappers.js';

const initialMentorForm = {
  bio: '',
  experienceYears: 1,
  skills: '',
  hourlyRate: 500,
};

const normalizeNotifications = (response) => {
  if (Array.isArray(response)) {
    return response;
  }

  if (Array.isArray(response?.content)) {
    return response.content;
  }

  if (Array.isArray(response?.data)) {
    return response.data;
  }

  return [];
};

const getDateLabel = (dateValue) => {
  if (!dateValue) {
    return 'Date pending';
  }

  const date = new Date(dateValue);

  if (Number.isNaN(date.getTime())) {
    return String(dateValue);
  }

  return date.toLocaleDateString('en-IN', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  });
};

const getSessionLink = (sessionId) => `https://meet.jit.si/skillsync-session-${sessionId}`;

const canCompleteSession = (session) => {
  if (session.status !== 'ACCEPTED' || !session.raw?.sessionDate) {
    return false;
  }

  return String(session.raw.sessionDate).slice(0, 10) <= new Date().toISOString().slice(0, 10);
};

function DashboardPage({ currentUser }) {
  const navigate = useNavigate();
  const isMentorRole = currentUser.role === 'ROLE_MENTOR';
  const [mentorForm, setMentorForm] = useState(initialMentorForm);
  const [mentorMessage, setMentorMessage] = useState('');
  const [mentorError, setMentorError] = useState('');
  const [mentorApplicationOverride, setMentorApplicationOverride] = useState(null);
  const [isReapplyMode, setIsReapplyMode] = useState(false);
  const [selectedStat, setSelectedStat] = useState(null);
  const [selectedMentor, setSelectedMentor] = useState(null);
  const [showTodayPlan, setShowTodayPlan] = useState(false);
  const [reviewSession, setReviewSession] = useState(null);
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [reviewMessage, setReviewMessage] = useState('');
  const [sessionActionError, setSessionActionError] = useState('');

  const { data: mentorsResponse } = useGetMentorsQuery();
  const { data: usersResponse } = useGetUsersQuery(undefined, {
    skip: currentUser.role !== 'ROLE_ADMIN',
  });
  const { data: myGroupsResponse } = useGetMyGroupsQuery(
    { userId: currentUser.userId },
    { skip: !currentUser?.userId }
  );
  const { data: notificationsResponse } = useGetNotificationsByUserQuery(currentUser.userId, {
    skip: !currentUser?.userId,
    pollingInterval: 15000,
    refetchOnFocus: true,
  });
  const { data: mentorRecord, error: mentorRecordError } = useGetMentorByUserIdQuery(currentUser.userId, {
    skip: !currentUser?.userId || !isMentorRole,
  });
  const [applyAsMentor, { isLoading: isApplying }] = useApplyAsMentorMutation();
  const [reapplyMentor, { isLoading: isReapplying }] = useReapplyMentorMutation();
  const [updateMentor, { isLoading: isUpdatingMentor }] = useUpdateMentorMutation();
  const [markNotificationRead] = useMarkNotificationReadMutation();
  const [completeSession, { isLoading: isCompletingSession }] = useCompleteSessionMutation();
  const [createReview, { isLoading: isSubmittingReview }] = useCreateReviewMutation();
  const { data: learnerReviewsResponse } = useGetReviewsByLearnerQuery(currentUser.userId, {
    skip: !currentUser?.userId || isMentorRole,
  });

  const userMap = useMemo(() => buildUserMap(usersResponse?.content || []), [usersResponse]);
  const mentors = useMemo(
    () => (mentorsResponse?.content || []).map((mentor) => mapMentorToCard(mentor, userMap)),
    [mentorsResponse, userMap]
  );
  const mentorDisplayMap = useMemo(
    () => new Map(mentors.map((mentor) => [String(mentor.id), mentor])),
    [mentors]
  );

  const effectiveMentorRecord = mentorApplicationOverride || mentorRecord;
  const hasNoMentorApplication = isMentorRole && !effectiveMentorRecord && mentorRecordError?.status === 404;
  const mentorApplicationStatus = effectiveMentorRecord?.status;
  const canShowMentorWorkspace = isMentorRole && mentorApplicationStatus === 'APPROVED';

  const { data: learnerSessionsResponse, isLoading: isLearnerSessionsLoading } = useGetSessionsByUserQuery(currentUser.userId, {
    skip: !currentUser?.userId || isMentorRole,
  });
  const { data: mentorSessionsResponse, isLoading: isMentorSessionsLoading } = useGetSessionsByMentorQuery(effectiveMentorRecord?.id, {
    skip: !effectiveMentorRecord?.id || !canShowMentorWorkspace,
  });

  const sessionsSource = useMemo(
    () => (canShowMentorWorkspace ? mentorSessionsResponse || [] : learnerSessionsResponse || []),
    [canShowMentorWorkspace, learnerSessionsResponse, mentorSessionsResponse]
  );
  const sessionRows = useMemo(
    () => sessionsSource.map((session) => mapSessionToRow(session, mentorDisplayMap, userMap, currentUser.roleLabel)),
    [currentUser.roleLabel, mentorDisplayMap, sessionsSource, userMap]
  );

  const myGroups = myGroupsResponse?.content || [];
  const notifications = useMemo(() => normalizeNotifications(notificationsResponse), [notificationsResponse]);
  const completedSessions = sessionsSource.filter((session) => session.status === 'COMPLETED').length;
  const mentorManagedGroups = canShowMentorWorkspace
    ? myGroups.filter((group) => Number(group.creatorUserId) === Number(currentUser.userId))
    : [];
  const mentorOwnedGroups = mentorManagedGroups.length ? mentorManagedGroups : canShowMentorWorkspace ? myGroups : [];
  const sortedMentorSessions = canShowMentorWorkspace
    ? [...sessionsSource].sort((left, right) => new Date(left.sessionDate).getTime() - new Date(right.sessionDate).getTime())
    : [];
  const todayKey = new Date().toISOString().slice(0, 10);
  const todaysMentorSessions = sortedMentorSessions.filter((session) => String(session.sessionDate).slice(0, 10) === todayKey);
  const upcomingMentorSessions = sortedMentorSessions.filter((session) => String(session.sessionDate).slice(0, 10) >= todayKey);
  const nextSession = upcomingMentorSessions[0];
  const upcomingSessionDates = Array.from(
    new Map(
      upcomingMentorSessions.slice(0, 5).map((session) => [
        String(session.sessionDate),
        {
          date: session.sessionDate,
          topic: session.topic,
          learnerId: session.learnerId,
          status: session.status,
        },
      ])
    ).values()
  );

  const stats = canShowMentorWorkspace
    ? [
        { label: 'Today\'s Sessions', value: String(todaysMentorSessions.length), change: 'Sessions scheduled for today', icon: 'calendar' },
        {
          label: 'Active Learners',
          value: String(new Set(sessionsSource.map((session) => session.learnerId)).size),
          change: 'Learners scheduled with you',
          icon: 'mentor',
        },
        {
          label: 'Average Rating',
          value: effectiveMentorRecord?.averageRating ? effectiveMentorRecord.averageRating.toFixed(1) : 'New',
          change: `${effectiveMentorRecord?.totalReviews || 0} reviews`,
          icon: 'star',
        },
        {
          label: 'Managed Groups',
          value: String(mentorOwnedGroups.length),
          change: 'Groups you are handling',
          icon: 'group',
        },
      ]
    : [
        { label: 'Upcoming Sessions', value: String(sessionsSource.length), change: 'Your current bookings', icon: 'calendar' },
        { label: 'Connected Mentors', value: String(mentors.length), change: 'Available on the platform', icon: 'mentor' },
        { label: 'Active Groups', value: String(myGroups.length), change: 'Groups you have joined', icon: 'group' },
        { label: 'Sessions Completed', value: String(completedSessions), change: 'Tracked from your history', icon: 'star' },
      ];

  const learnerFocus = [
    sessionsSource[0]
      ? `Prepare for your next session on ${sessionsSource[0].topic}.`
      : 'Book your first mentor session to start learning with a guide.',
    myGroups[0]
      ? `Stay active in ${myGroups[0].name} and keep the discussion moving.`
      : 'Join a learning group to stay accountable and learn with peers.',
    mentors[0]
      ? `Explore mentor profiles like ${mentors[0].name} and compare specialities.`
      : 'Mentor profiles will appear here once mentor data is available.',
  ];

  const mentorLearnerList = Array.from(new Set((mentorSessionsResponse || []).map((session) => session.learnerId)))
    .map((id) => userMap.get(String(id)) || {
      id,
      name: `Learner #${id}`,
      email: 'Learner details are restricted',
      role: 'ROLE_LEARNER',
    });
  const mentorLearners = mentorLearnerList.slice(0, 5);
  const completedSessionRows = sessionRows.filter((session) => session.status === 'COMPLETED');
  const reviewedSessionIds = useMemo(
    () => new Set((learnerReviewsResponse || []).map((review) => Number(review.sessionId))),
    [learnerReviewsResponse]
  );

  const handleNotificationRead = async (notificationId, isRead) => {
    if (isRead) {
      return;
    }

    await markNotificationRead(notificationId).unwrap();
  };

  const handleCompleteSession = async (sessionId) => {
    try {
      setSessionActionError('');
      await completeSession(sessionId).unwrap();
    } catch (error) {
      setSessionActionError(error?.data?.message || error?.data?.error || error?.message || 'Unable to complete session.');
    }
  };

  const handleOpenReview = (session) => {
    setReviewMessage('');
    setSessionActionError('');
    setReviewForm({ rating: 5, comment: '' });
    setReviewSession(session);
  };

  const handleSubmitReview = async () => {
    if (!reviewSession) {
      return;
    }

    try {
      setSessionActionError('');
      setReviewMessage('');
      await createReview({
        mentorId: reviewSession.mentorId,
        learnerId: Number(currentUser.userId),
        sessionId: reviewSession.id,
        rating: Number(reviewForm.rating),
        comment: reviewForm.comment.trim(),
      }).unwrap();
      setReviewMessage('Review submitted successfully.');
      setReviewSession(null);
    } catch (error) {
      setSessionActionError(error?.data?.message || error?.data?.error || error?.message || 'Unable to submit review.');
    }
  };

  const handleApplyAsMentor = async () => {
    if (!mentorForm.bio.trim() || !mentorForm.skills.trim()) {
      setMentorError('Please add your bio and at least one skill.');
      return;
    }

    try {
      setMentorError('');
      setMentorMessage('');

      const payload = {
        bio: mentorForm.bio.trim(),
        experienceYears: Number(mentorForm.experienceYears),
        skills: mentorForm.skills.split(',').map((item) => item.trim()).filter(Boolean),
        hourlyRate: Number(mentorForm.hourlyRate),
        applicantName: currentUser.name,
        applicantEmail: currentUser.email,
        userId: Number(currentUser.userId),
      };

      if (isReapplyMode && effectiveMentorRecord?.id) {
        await updateMentor({
          mentorId: effectiveMentorRecord.id,
          payload,
        }).unwrap();

        const response = await reapplyMentor(effectiveMentorRecord.id).unwrap();
        setMentorApplicationOverride(response);
        setMentorMessage('Mentor application resubmitted. Your status is now pending review.');
        setIsReapplyMode(false);
      } else {
        const response = await applyAsMentor(payload).unwrap();
        setMentorApplicationOverride(response);
        setMentorMessage('Mentor application submitted successfully. Your status is now pending admin approval.');
      }

      setMentorForm(initialMentorForm);
    } catch (error) {
      setMentorError(error?.data?.message || error?.data?.error || error?.message || 'Unable to submit mentor application.');
    }
  };

  const handleReapply = () => {
    setMentorError('');
    setMentorMessage('');
    setIsReapplyMode(true);
    setMentorForm({
      bio: effectiveMentorRecord?.bio || '',
      experienceYears: effectiveMentorRecord?.experienceYears || 1,
      skills: effectiveMentorRecord?.skills?.join(', ') || '',
      hourlyRate: effectiveMentorRecord?.hourlyRate || 500,
    });
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
          subtitle="Details for the selected dashboard metric."
        />
        <div className="detail-list">
          {selectedStat === 'Today\'s Sessions' &&
            (todaysMentorSessions.length
              ? todaysMentorSessions.map((session) => {
                  const learner = userMap.get(String(session.learnerId));

                  return (
                    <article className="detail-item" key={session.id}>
                      <div>
                        <h3>{session.topic}</h3>
                        <p>{learner?.name || `Learner #${session.learnerId}`} • {getDateLabel(session.sessionDate)}</p>
                      </div>
                      <div className="detail-meta">
                        <strong>{session.status}</strong>
                        <span>{session.duration} min</span>
                      </div>
                    </article>
                  );
                })
              : renderEmptyState('No sessions are scheduled for today.'))}

          {selectedStat === 'Active Learners' &&
            (mentorLearnerList.length
              ? mentorLearnerList.map((learner) => (
                  <article className="detail-item" key={learner.id}>
                    <div>
                      <h3>{learner.name}</h3>
                      <p>{learner.email}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{learner.role}</strong>
                      <span>Connected learner</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('No learners are linked to your sessions yet.'))}

          {selectedStat === 'Managed Groups' &&
            (mentorOwnedGroups.length
              ? mentorOwnedGroups.map((group) => (
                  <article className="detail-item" key={group.id}>
                    <div>
                      <h3>{group.name}</h3>
                      <p>{(group.topics || []).join(', ') || group.description || 'General learning group'}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{group.memberCount} learners</strong>
                      <span>{group.status}</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('You are not handling any groups yet.'))}

          {selectedStat === 'Average Rating' ? (
            <article className="detail-item">
              <div>
                <h3>{effectiveMentorRecord?.averageRating ? `${effectiveMentorRecord.averageRating.toFixed(1)} average rating` : 'No rating yet'}</h3>
                <p>{effectiveMentorRecord?.totalReviews || 0} review(s) received.</p>
              </div>
              <div className="detail-meta">
                <strong>{effectiveMentorRecord?.status || 'No profile'}</strong>
                <span>{effectiveMentorRecord?.isAvailable ? 'Available' : 'Unavailable'}</span>
              </div>
            </article>
          ) : null}

          {selectedStat === 'Upcoming Sessions' &&
            (sessionRows.length
              ? sessionRows.map((session) => (
                  <article className="detail-item" key={session.id}>
                    <div>
                      <h3>{session.topic}</h3>
                      <p>{session.mentor} • {session.date}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{session.status}</strong>
                      <span>{session.duration}</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('No upcoming sessions found.'))}

          {selectedStat === 'Connected Mentors' &&
            (mentors.length
              ? mentors.map((mentor) => (
                  <article className="detail-item" key={mentor.id}>
                    <div>
                      <h3>{mentor.name}</h3>
                      <p>{mentor.skills.join(', ') || 'No skills added'}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{mentor.status}</strong>
                      <span>{mentor.price}</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('No mentors are available yet.'))}

          {selectedStat === 'Active Groups' &&
            (myGroups.length
              ? myGroups.map((group) => (
                  <article className="detail-item" key={group.id}>
                    <div>
                      <h3>{group.name}</h3>
                      <p>{group.description || 'No description added.'}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{group.memberCount} members</strong>
                      <span>{group.status}</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('You have not joined any groups yet.'))}

          {selectedStat === 'Sessions Completed' &&
            (completedSessionRows.length
              ? completedSessionRows.map((session) => (
                  <article className="detail-item" key={session.id}>
                    <div>
                      <h3>{session.topic}</h3>
                      <p>{session.mentor} • {session.date}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{session.status}</strong>
                      <span>{session.duration}</span>
                    </div>
                  </article>
                ))
              : renderEmptyState('No completed sessions found yet.'))}
        </div>
      </section>
    );
  };

  const renderNotifications = () => (
    <section className="card dashboard-panel">
      <SectionHeading
        title="🔔 Notifications"
        subtitle="Recent alerts and updates for your account."
      />
      <div className="detail-list">
        {notifications.slice(0, 4).map((notification) => {
          const alreadyRead = notification.isRead ?? notification.read;

          return (
            <article className="detail-item" key={notification.id}>
              <div>
                <h3>{notification.subject || notification.type}</h3>
                <p>{notification.message}</p>
              </div>
              <div className="detail-meta">
                {/* <strong>{notification.status}</strong> */}
                <button
                  className={alreadyRead ? 'success-button' : 'secondary-button'}
                  onClick={() => handleNotificationRead(notification.id, alreadyRead)}
                  type="button"
                >
                  {alreadyRead ? 'Read' : 'Mark Read'}
                </button>
              </div>
            </article>
          );
        })}
        {!notifications.length ? <div className="activity-item">No notifications available yet.</div> : null}
      </div>
    </section>
  );

  const renderMentorSessionActions = (session) => (
    <div className="button-row">
      {session.status === 'ACCEPTED' ? (
        <a className="secondary-button" href={getSessionLink(session.id)} rel="noreferrer" target="_blank">
          Join
        </a>
      ) : null}
      {canCompleteSession(session) ? (
        <button
          className="success-button"
          disabled={isCompletingSession}
          onClick={() => handleCompleteSession(session.id)}
          type="button"
        >
          Complete
        </button>
      ) : null}
    </div>
  );

  const renderLearnerSessionActions = (session) => {
    const alreadyReviewed = reviewedSessionIds.has(Number(session.id));

    return (
      <div className="button-row">
        {session.status === 'ACCEPTED' ? (
          <a className="secondary-button" href={getSessionLink(session.id)} rel="noreferrer" target="_blank">
            Join
          </a>
        ) : null}
        {session.status === 'COMPLETED' ? (
          <button
            className={alreadyReviewed ? 'ghost-button' : 'success-button'}
            disabled={alreadyReviewed || isSubmittingReview}
            onClick={() => handleOpenReview(session)}
            type="button"
          >
            {alreadyReviewed ? 'Reviewed' : 'Add Review'}
          </button>
        ) : null}
      </div>
    );
  };

  if (isMentorRole && (hasNoMentorApplication || isReapplyMode)) {
    return (
      <div className="page-stack">
        <section className="hero-strip">
          <div>
            <p className="eyebrow">{isReapplyMode ? '🔁 Reapply as Mentor' : '👨‍🏫 Mentor Application'}</p>
            <h2>{isReapplyMode ? 'Update your mentor profile and resubmit it' : 'Apply to become a mentor on SkillSync'}</h2>
          </div>
        </section>

        <section className="card dashboard-panel">
          <SectionHeading
            title={isReapplyMode ? 'Resubmit your mentor application' : 'Submit your mentor application'}
            subtitle="Your profile will remain in pending state until an admin approves it."
          />
          <div className="booking-section">
            <h4>📝 Bio</h4>
            <input value={mentorForm.bio} onChange={(event) => setMentorForm((current) => ({ ...current, bio: event.target.value }))} />
          </div>
          <div className="booking-section">
            <h4>💼 Experience Years</h4>
            <input
              type="number"
              value={mentorForm.experienceYears}
              onChange={(event) => setMentorForm((current) => ({ ...current, experienceYears: event.target.value }))}
            />
          </div>
          <div className="booking-section">
            <h4>🏷️ Skills</h4>
            <input
              placeholder="Java, Spring Boot, DSA"
              value={mentorForm.skills}
              onChange={(event) => setMentorForm((current) => ({ ...current, skills: event.target.value }))}
            />
          </div>
          <div className="booking-section">
            <h4>💰 Hourly Rate</h4>
            <input
              type="number"
              value={mentorForm.hourlyRate}
              onChange={(event) => setMentorForm((current) => ({ ...current, hourlyRate: event.target.value }))}
            />
          </div>
          {mentorMessage ? <p className="success-text">{mentorMessage}</p> : null}
          {mentorError ? <p className="muted-text">{mentorError}</p> : null}
          <button
            className="primary-button"
            onClick={handleApplyAsMentor}
            type="button"
            disabled={isApplying || isReapplying || isUpdatingMentor}
          >
            {isApplying || isReapplying || isUpdatingMentor
              ? 'Submitting...'
              : isReapplyMode
                ? 'Resubmit Application'
                : 'Apply as Mentor'}
          </button>
        </section>

        {renderNotifications()}
      </div>
    );
  }

  if (isMentorRole && mentorApplicationStatus === 'PENDING') {
    return (
      <div className="page-stack">
        <section className="hero-strip">
          <div>
            <p className="eyebrow">⏳ Application Pending</p>
            <h2>Your mentor application is waiting for admin approval.</h2>
          </div>
        </section>

        <section className="card dashboard-panel">
          <SectionHeading
            title="⏳ Pending review"
            subtitle="Until approval, your mentor dashboard stays locked and learners cannot book you yet."
          />
          <div className="summary-list">
            <div><span>Status</span><strong>PENDING</strong></div>
            <div><span>Bio</span><strong>{effectiveMentorRecord.bio}</strong></div>
            <div><span>Skills</span><strong>{effectiveMentorRecord.skills?.join(', ') || 'No skills added'}</strong></div>
            <div><span>Hourly Rate</span><strong>Rs {effectiveMentorRecord.hourlyRate}/hr</strong></div>
          </div>
        </section>

        {renderNotifications()}
      </div>
    );
  }

  if (isMentorRole && mentorApplicationStatus === 'REJECTED') {
    return (
      <div className="page-stack">
        <section className="hero-strip">
          <div>
            <p className="eyebrow">🚫 Application Rejected</p>
            <h2>Your mentor application needs updates before approval.</h2>
          </div>
        </section>

        <section className="card dashboard-panel">
          <SectionHeading
            title="🚫 Rejected application"
            subtitle="The admin has rejected this mentor profile. Review the reason below and reapply."
          />
          <div className="summary-list">
            <div><span>Status</span><strong>REJECTED</strong></div>
            <div><span>Reason</span><strong>{effectiveMentorRecord.rejectionReason || 'No rejection reason provided.'}</strong></div>
            <div><span>Bio</span><strong>{effectiveMentorRecord.bio}</strong></div>
            <div><span>Skills</span><strong>{effectiveMentorRecord.skills?.join(', ') || 'No skills added'}</strong></div>
          </div>
          {mentorMessage ? <p className="success-text">{mentorMessage}</p> : null}
          {mentorError ? <p className="muted-text">{mentorError}</p> : null}
          <button className="primary-button" onClick={handleReapply} type="button" disabled={isReapplying}>
            {isReapplying ? 'Resubmitting...' : 'Reapply for Mentor Approval'}
          </button>
        </section>

        {renderNotifications()}
      </div>
    );
  }

  return (
    <div className="page-stack">
      <section className="hero-strip">
        <div>
          <p className="eyebrow">Good morning, {currentUser.name.split(' ')[0]}</p>
          <h2>{currentUser.headline}</h2>
        </div>
        <button
          className="primary-button"
          onClick={() => {
            if (canShowMentorWorkspace) {
              setShowTodayPlan((current) => !current);
            } else {
              setSelectedStat('Upcoming Sessions');
            }
          }}
          type="button"
        >
          {canShowMentorWorkspace ? 'Manage Today' : 'Plan This Week'}
        </button>
      </section>

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

      {canShowMentorWorkspace && showTodayPlan ? (
        <section className="card dashboard-panel">
          <SectionHeading
            title="📅 Manage Today"
            subtitle="Today's accepted and requested sessions, ordered by schedule."
          />
          <div className="detail-list">
            {todaysMentorSessions.map((session) => (
              <article className="detail-item" key={session.id}>
                <div>
                  <h3>{session.topic}</h3>
                  <p>{session.learnerName || `Learner #${session.learnerId}`} • {session.requiredSkill}</p>
                </div>
                <div className="detail-meta">
                  <strong>{session.status}</strong>
                  <a className="secondary-button" href={getSessionLink(session.id)} rel="noreferrer" target="_blank">
                    Join Call
                  </a>
                </div>
              </article>
            ))}
            {!todaysMentorSessions.length ? <div className="activity-item">No sessions lined up for today.</div> : null}
          </div>
        </section>
      ) : null}

      {canShowMentorWorkspace ? (
        <>
          <div className="dashboard-duo-grid">
            <section className="card dashboard-panel">
              <SectionHeading
                title="👥 Your Batches"
                subtitle="Groups and learning communities you are directly handling."
              />
              <div className="detail-list">
                {mentorOwnedGroups.slice(0, 4).map((group) => (
                  <article className="detail-item" key={group.id}>
                    <div>
                      <h3>{group.name}</h3>
                      <p>{(group.topics || []).join(', ') || 'General learning group'}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{group.memberCount} learners</strong>
                      <span>{group.status}</span>
                    </div>
                  </article>
                ))}
                {!mentorOwnedGroups.length ? <div className="activity-item">You are not handling any groups yet.</div> : null}
              </div>
            </section>

            <aside className="card dashboard-panel">
              <SectionHeading
                title="📅 Your Schedule"
                subtitle="Your own mentoring timeline instead of platform-wide availability."
              />
              <div className="summary-list">
                <div>
                  <span>Next Session</span>
                  <strong>{nextSession ? getDateLabel(nextSession.sessionDate) : 'No upcoming sessions'}</strong>
                </div>
                <div>
                  <span>Next Topic</span>
                  <strong>{nextSession?.topic || 'No topic scheduled yet'}</strong>
                </div>
                <div>
                  <span>Today</span>
                  <strong>{todaysMentorSessions.length ? `${todaysMentorSessions.length} session(s)` : 'No sessions today'}</strong>
                </div>
                <div>
                  <span>Upcoming Dates</span>
                  <strong>{upcomingMentorSessions.length}</strong>
                </div>
              </div>
            </aside>
          </div>

          <div className="dashboard-duo-grid">
            <section className="card dashboard-panel">
              <SectionHeading
                title="📆 Today and Upcoming Dates"
                subtitle="Keep the next few mentoring dates visible at a glance."
              />
              <div className="detail-list">
                {upcomingSessionDates.map((session) => {
                  const learner = userMap.get(String(session.learnerId));

                  return (
                    <article className="detail-item" key={`${session.date}-${session.learnerId}-${session.topic}`}>
                      <div>
                        <h3>{getDateLabel(session.date)}</h3>
                        <p>{session.topic}</p>
                      </div>
                      <div className="detail-meta">
                        <strong>{learner?.name || `Learner #${session.learnerId}`}</strong>
                        <span>{session.status}</span>
                      </div>
                    </article>
                  );
                })}
                {!upcomingSessionDates.length ? <div className="activity-item">Upcoming session dates will appear here once bookings are scheduled.</div> : null}
              </div>
            </section>

            <section className="card dashboard-panel">
              <SectionHeading
                title="👨‍🏫 Mentor Console"
                subtitle="Your mentoring profile and readiness details."
              />
              <div className="summary-list">
                <div>
                  <span>Speciality</span>
                  <strong>{effectiveMentorRecord?.skills?.join(', ') || 'Not added yet'}</strong>
                </div>
                <div>
                  <span>Hourly Rate</span>
                  <strong>{effectiveMentorRecord ? `Rs ${effectiveMentorRecord.hourlyRate}/hr` : 'Not set'}</strong>
                </div>
                <div>
                  <span>Status</span>
                  <strong>{effectiveMentorRecord?.status || 'No mentor profile found'}</strong>
                </div>
                <div>
                  <span>Bookings</span>
                  <strong>{effectiveMentorRecord?.isAvailable ? 'Open for learner bookings' : 'Bookings paused'}</strong>
                </div>
              </div>
            </section>
          </div>

          <section className="card dashboard-panel">
            <SectionHeading
              title="🎓 Active Learners"
              subtitle="Learners currently connected to your sessions."
            />
            <div className="detail-list">
              {mentorLearners.map((learner) => (
                <article className="detail-item" key={learner.id}>
                  <div>
                    <h3>{learner.name}</h3>
                    <p>{learner.email}</p>
                  </div>
                  <div className="detail-meta">
                    <strong>{learner.role}</strong>
                    <span>Connected learner</span>
                  </div>
                </article>
              ))}
              {!mentorLearners.length ? <div className="activity-item">No learners are linked to your sessions yet.</div> : null}
            </div>
          </section>

          {renderNotifications()}

          <section>
            <SectionHeading
              title="📋 Session Queue"
              subtitle="Your upcoming and recent mentoring sessions."
            />
            {isMentorSessionsLoading ? (
              <div className="card">Loading sessions...</div>
            ) : (
              <SessionTable
                participantLabel="Learner"
                renderActions={renderMentorSessionActions}
                sessions={sessionRows}
              />
            )}
          </section>
        </>
      ) : (
        <>
          <section>
            <SectionHeading
              title="👨‍🏫 Recommended Mentors"
              subtitle="Experts aligned with your current learning goals."
            />
              <div className="mentor-grid">
                {mentors.slice(0, 3).map((mentor) => (
                <MentorCard
                  compact
                  key={mentor.id}
                  mentor={mentor}
                  onPrimaryAction={() => navigate(`/booking?mentorId=${mentor.id}`)}
                  onSecondaryAction={() => setSelectedMentor(mentor)}
                />
              ))}
            </div>
          </section>

          <div className="dashboard-duo-grid">
            <section className="card dashboard-panel">
              <SectionHeading
                title="🎯 Your Weekly Focus"
                subtitle="Real next steps based on your sessions and groups."
              />
              <div className="focus-list">
                {learnerFocus.map((item) => (
                  <div className="activity-item" key={item}>
                    {item}
                  </div>
                ))}
              </div>
            </section>

            <section className="card dashboard-panel">
              <SectionHeading
                title="📅 Upcoming Sessions"
                subtitle="Keep your next mentor sessions visible and easy to follow."
              />
              {isLearnerSessionsLoading ? (
                <div>Loading sessions...</div>
              ) : (
                <SessionTable renderActions={renderLearnerSessionActions} sessions={sessionRows} />
              )}
            </section>
          </div>

          {renderNotifications()}
          {sessionActionError ? <p className="muted-text">{sessionActionError}</p> : null}
          {reviewMessage ? <p className="success-text">{reviewMessage}</p> : null}

          <div className="dashboard-duo-grid">
            <section className="card dashboard-panel">
              <SectionHeading
                title="👥 Joined Groups"
                subtitle="Pulled from the group service."
              />
              <div className="detail-list">
                {myGroups.slice(0, 4).map((group) => (
                  <article className="detail-item" key={group.id}>
                    <div>
                      <h3>{group.name}</h3>
                      <p>{group.description || 'No description added.'}</p>
                    </div>
                    <div className="detail-meta">
                      <strong>{group.memberCount} members</strong>
                      <span>{group.status}</span>
                    </div>
                  </article>
                ))}
                {!myGroups.length ? <div className="activity-item">You have not joined any groups yet.</div> : null}
              </div>
            </section>
          </div>
        </>
      )}

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
              <h4>🏷️ Skills</h4>
              <div className="tag-list">
                {selectedMentor.skills.map((skill) => (
                  <span className="tag" key={skill}>
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div>
              <h4>📝 Bio</h4>
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

      {reviewSession ? (
        <div className="modal-backdrop" role="presentation">
          <div className="modal-card" role="dialog" aria-modal="true" aria-labelledby="review-title">
            <div className="panel-title-row">
              <div>
                <p className="eyebrow">⭐ Session Review</p>
                <h3 id="review-title">Rate {reviewSession.mentor}</h3>
              </div>
              <button className="ghost-button" onClick={() => setReviewSession(null)} type="button">
                Close
              </button>
            </div>

            <div className="booking-section">
              <h4>⭐ Rating</h4>
              <select
                value={reviewForm.rating}
                onChange={(event) => setReviewForm((current) => ({ ...current, rating: event.target.value }))}
              >
                {[5, 4, 3, 2, 1].map((rating) => (
                  <option key={rating} value={rating}>
                    {rating} star{rating > 1 ? 's' : ''}
                  </option>
                ))}
              </select>
            </div>

            <div className="booking-section">
              <h4>📝 Review</h4>
              <input
                placeholder="Share your feedback"
                value={reviewForm.comment}
                onChange={(event) => setReviewForm((current) => ({ ...current, comment: event.target.value }))}
              />
            </div>

            <div className="button-row modal-actions">
              <button className="ghost-button" onClick={() => setReviewSession(null)} type="button">
                Cancel
              </button>
              <button className="primary-button" disabled={isSubmittingReview} onClick={handleSubmitReview} type="button">
                {isSubmittingReview ? 'Submitting...' : '⭐ Submit Review'}
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default DashboardPage;
