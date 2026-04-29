import { useMemo, useState } from 'react';

import {
  useAcceptSessionMutation,
  useGetNotificationsByUserQuery,
  useGetUnreadNotificationCountQuery,
  useMarkNotificationReadMutation,
  useRejectSessionMutation,
} from '../features/platform/platformApi.js';

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

const getTodayLabel = () =>
  new Date().toLocaleDateString('en-IN', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });

function Topbar({ currentUser, onLogout, pageTitle }) {
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const { data: unreadData } = useGetUnreadNotificationCountQuery(currentUser.userId, {
    skip: !currentUser?.userId,
    pollingInterval: 15000,
    refetchOnFocus: true,
  });
  const { data: notificationsResponse = [] } = useGetNotificationsByUserQuery(currentUser.userId, {
    skip: !currentUser?.userId,
    pollingInterval: 15000,
    refetchOnFocus: true,
  });
  const [markNotificationRead, { isLoading: isMarkingRead }] = useMarkNotificationReadMutation();
  const [acceptSession, { isLoading: isAcceptingSession }] = useAcceptSessionMutation();
  const [rejectSession, { isLoading: isRejectingSession }] = useRejectSessionMutation();

  const notifications = useMemo(
    () => normalizeNotifications(notificationsResponse).slice(0, 8),
    [notificationsResponse]
  );

  const handleMarkRead = async (notificationId, alreadyRead) => {
    if (alreadyRead) {
      return;
    }

    await markNotificationRead(notificationId).unwrap();
  };

  const handleSessionDecision = async (notification, action) => {
    if (!notification?.referenceId) {
      return;
    }

    if (action === 'accept') {
      await acceptSession(notification.referenceId).unwrap();
    } else {
      await rejectSession(notification.referenceId).unwrap();
    }

    if (!notification.isRead) {
      await markNotificationRead(notification.id).unwrap();
    }
  };

  const showSessionActions = (notification) =>
    currentUser.role === 'ROLE_MENTOR' &&
    notification.type === 'SESSION_BOOKED' &&
    !notification.isRead;

  return (
    <>
      <header className="topbar">
        <div>
          <h1>Greetings, {currentUser.name.split(' ')[0]}! 👋</h1>
          <p>{getTodayLabel()} — Here&apos;s your {pageTitle.toLowerCase()} overview</p>
        </div>

        <div className="topbar-actions">
          {typeof unreadData?.unreadCount === 'number' ? (
            <button className="user-pill notification-pill notification-trigger" onClick={() => setIsNotificationsOpen(true)} type="button">
              <strong>🔔</strong>
              {unreadData.unreadCount ? <span>{unreadData.unreadCount}</span> : null}
            </button>
          ) : null}

          <button className="user-pill notification-trigger" onClick={() => setIsProfileOpen(true)} type="button">
            <div className="avatar-badge small">{currentUser.initials}</div>
          </button>

          <button className="ghost-button" onClick={onLogout} type="button">
            Logout
          </button>
        </div>
      </header>

      {isNotificationsOpen ? (
        <div className="modal-backdrop" role="presentation">
          <div className="modal-card notification-modal" role="dialog" aria-modal="true" aria-labelledby="notifications-title">
            <div className="panel-title-row">
              <div>
                <p className="eyebrow">📥 Inbox</p>
                <h3 id="notifications-title">🔔 Notifications</h3>
              </div>
              <button className="ghost-button" onClick={() => setIsNotificationsOpen(false)} type="button">
                Close
              </button>
            </div>

            <div className="detail-list">
              {notifications.map((notification) => {
                const alreadyRead = notification.isRead ?? notification.read;

                return (
                  <article className="detail-item" key={notification.id}>
                    <div>
                      <h3>{notification.subject || notification.type}</h3>
                      <p>{notification.message}</p>
                    </div>
                    <div className="detail-meta">
                      {/* <strong>{alreadyRead ? 'Read' : 'Unread'}</strong> */}

                      {/* <span>{alreadyRead ? 'Read' : 'Unread'}</span> */}
                      <div className="button-row notification-actions">
                        {showSessionActions(notification) ? (
                          <>
                            <button
                              className="success-button"
                              onClick={() => handleSessionDecision(notification, 'accept')}
                              type="button"
                              disabled={isAcceptingSession || isRejectingSession || isMarkingRead}
                            >
                              Approve Session
                            </button>
                            <button
                              className="secondary-button"
                              onClick={() => handleSessionDecision(notification, 'reject')}
                              type="button"
                              disabled={isAcceptingSession || isRejectingSession || isMarkingRead}
                            >
                              Reject Session
                            </button>
                          </>
                        ) : (
                          <button
                            className={alreadyRead ? 'ghost-button' : 'secondary-button'}
                            onClick={() => handleMarkRead(notification.id, alreadyRead)}
                            type="button"
                            disabled={isMarkingRead}
                          >
                            {alreadyRead ? 'Read' : 'Mark Read'}
                          </button>
                        )}
                      </div>
                    </div>
                  </article>
                );
              })}
              {!notifications.length ? <div className="activity-item">No notifications yet.</div> : null}
            </div>
          </div>
        </div>
      ) : null}

      {isProfileOpen ? (
        <div className="modal-backdrop" role="presentation">
          <div className="modal-card" role="dialog" aria-modal="true" aria-labelledby="profile-title">
            <div className="panel-title-row">
              <div>
                <p className="eyebrow">👤 Profile</p>
                <h3 id="profile-title">{currentUser.name}</h3>
              </div>
              <button className="ghost-button" onClick={() => setIsProfileOpen(false)} type="button">
                Close
              </button>
            </div>

            <div className="summary-list">
              <div><span>Name</span><strong>{currentUser.name}</strong></div>
              <div><span>Email</span><strong>{currentUser.email || 'Not available'}</strong></div>
              <div><span>Role</span><strong>{currentUser.roleLabel || currentUser.role}</strong></div>
              <div><span>User ID</span><strong>{currentUser.userId}</strong></div>
              <div><span>Headline</span><strong>{currentUser.headline || 'No headline added'}</strong></div>
            </div>
          </div>
        </div>
      ) : null}
    </>
  );
}

export default Topbar;
