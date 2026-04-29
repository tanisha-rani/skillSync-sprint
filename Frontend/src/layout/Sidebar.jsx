import { useNavigate } from 'react-router-dom';

function Sidebar({ activeRoute, currentUser, navigation }) {
  const navigate = useNavigate();

  return (
    <aside className="sidebar">
      <div>
        <div className="brand-mark">
          Skill<span>Sync</span>
        </div>
        <p className="sidebar-copy">{currentUser.roleLabel} workspace</p>
      </div>

      <nav className="sidebar-nav" aria-label="Main navigation">
        {navigation.map((item) => (
          <button
            key={item.id}
            className={item.id === activeRoute ? 'nav-item active' : 'nav-item'}
            onClick={() => navigate(item.id === 'admin' ? '/admin' : `/${item.id}`)}
            type="button"
          >
            <span>{item.label}</span>
          </button>
        ))}
      </nav>

      <div className="sidebar-card">
        <div className="avatar-badge">{currentUser.initials}</div>
        <div>
          <h3>{currentUser.name}</h3>
          <p>{currentUser.headline}</p>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;
