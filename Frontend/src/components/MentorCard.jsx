function MentorCard({
  mentor,
  compact = false,
  primaryActionLabel = 'Book',
  secondaryActionLabel = 'View',
  onPrimaryAction,
  onSecondaryAction,
}) {
  return (
    <article className={compact ? 'card mentor-card compact' : 'card mentor-card'}>
      <div className="mentor-header">
        <div className="avatar-badge warm">{mentor.initials}</div>
        <div>
          <h3>{mentor.name}</h3>
          <p>{mentor.experience}</p>
        </div>
        <span className="status-pill">{mentor.status}</span>
      </div>

      <p className="mentor-rating">
        {'★'.repeat(5)} <span>{mentor.rating}</span> ({mentor.reviews} reviews)
      </p>

      <div className="tag-list">
        {mentor.skills.map((skill) => (
          <span key={skill} className="tag">
            {skill}
          </span>
        ))}
      </div>

      {!compact ? <p className="muted-text">{mentor.bio}</p> : null}

      <div className="mentor-footer">
        <strong>{mentor.price}</strong>
        <div className="button-row">
          <button className="secondary-button" onClick={() => onSecondaryAction?.(mentor)} type="button">
            {secondaryActionLabel}
          </button>
          <button className="primary-button" onClick={() => onPrimaryAction?.(mentor)} type="button">
            {primaryActionLabel}
          </button>
        </div>
      </div>
    </article>
  );
}

export default MentorCard;
