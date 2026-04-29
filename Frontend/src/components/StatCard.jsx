import { AppIcon } from '../utils/icons.jsx';

function StatCard({ item, onClick, isActive = false }) {
  return (
    <button
      className={`card stat-card ${isActive ? 'stat-card-active' : ''}`}
      onClick={onClick}
      type="button"
    >
      <div className="stat-icon">
        <AppIcon name={item.icon} />
      </div>
      <strong>{item.value}</strong>
      <span>{item.label}</span>
      <p>{item.change}</p>
    </button>
  );
}

export default StatCard;
