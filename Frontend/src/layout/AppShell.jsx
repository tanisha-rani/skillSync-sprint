import Sidebar from './Sidebar.jsx';
import Topbar from './Topbar.jsx';

function AppShell({
  activeRoute,
  children,
  currentUser,
  navigation,
  onLogout,
  pageTitle,
}) {
  return (
    <div className="app-shell">
      <Sidebar activeRoute={activeRoute} currentUser={currentUser} navigation={navigation} />
      <main className="app-main">
        <Topbar currentUser={currentUser} onLogout={onLogout} pageTitle={pageTitle} />
        <div className="page-content">{children}</div>
      </main>
    </div>
  );
}

export default AppShell;
