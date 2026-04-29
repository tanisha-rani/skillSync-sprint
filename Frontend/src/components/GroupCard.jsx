import { AppIcon } from '../utils/icons.jsx';

function GroupCard({ group, onJoin, onLeave, isJoining = false, isLeaving = false }) {
  const isBusy = isJoining || isLeaving;

  return (
    <article className="card group-card">
      <div className="group-main">
        <div className="group-topline">
          <div className="group-icon">
            <AppIcon name={group.icon} />
          </div>
          <div>
            <h3>{group.name}</h3>
            <p>
              {group.members} members 
            </p>
          </div>
        </div>

        <p className="group-description">{group.description}</p>

        <div className="tag-list">
          {group.tags.map((tag) => (
            <span key={tag} className="tag">
              {tag}
            </span>
          ))}
        </div>

        <div className="group-meta">
          <span>{group.activity}</span>
          <span>{group.posts}</span>
          <span>{group.access}</span>
        </div>
      </div>

      <div className="group-side">
        <strong>{group.members}</strong>
        <span>Members</span>
        <button
          className={group.joined ? 'secondary-button' : 'primary-button'}
          onClick={() => (group.joined ? onLeave?.(group) : onJoin?.(group))}
          type="button"
          disabled={isBusy}
        >
          {group.joined ? (isLeaving ? 'Leaving...' : '🚪 Leave Group') : isJoining ? 'Joining...' : '🤝 Join Group'}
        </button>
      </div>
    </article>
  );
}

export default GroupCard;
