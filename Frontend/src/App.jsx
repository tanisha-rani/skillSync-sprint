import { useEffect, useMemo } from 'react';
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';

import AppShell from './layout/AppShell.jsx';
import { appData, navigationByRole } from './data/mockData.js';
import ProtectedRoute from './app/ProtectedRoute.jsx';
import { clearCredentials } from './features/auth/authSlice.js';
import DashboardPage from './pages/DashboardPage.jsx';
import MentorsPage from './pages/MentorsPage.jsx';
import BookingPage from './pages/BookingPage.jsx';
import GroupsPage from './pages/GroupsPage.jsx';
import AdminPage from './pages/AdminPage.jsx';
import AdminSkillsPage from './pages/AdminSkillsPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import SignupPage from './pages/SignupPage.jsx';

const pageTitles = {
  dashboard: '🏠 Dashboard',
  mentors: '👨‍🏫 Find Mentors',
  booking: '📅 Book Session',
  groups: '👥 Learning Groups',
  admin: '🛡️ Admin Console',
  'skills-admin': '⭐ Skills',
};

const getActiveRoute = (pathname) => {
  if (pathname === '/' || pathname === '/dashboard') {
    return 'dashboard';
  }

  return pathname.replace('/', '') || 'dashboard';
};

function App() {
  const dispatch = useDispatch();
  const location = useLocation();
  const navigate = useNavigate();
  const authUser = useSelector((state) => state.auth.user);
  const isAuthenticated = Boolean(authUser);
  const role = authUser?.role || 'ROLE_LEARNER';
  const navigation = navigationByRole[role] || navigationByRole.ROLE_LEARNER;
  const currentUser = authUser || appData.users.ROLE_LEARNER;
  const activeRoute = getActiveRoute(location.pathname);

  useEffect(() => {
    if (!isAuthenticated) {
      return;
    }

    if (role === 'ROLE_MENTOR' && (location.pathname === '/mentors' || location.pathname === '/booking')) {
      navigate('/dashboard', { replace: true });
      return;
    }

    if (role === 'ROLE_ADMIN' && location.pathname === '/dashboard') {
      navigate('/admin', { replace: true });
    }
  }, [isAuthenticated, location.pathname, navigate, role]);

  const routeContent = useMemo(() => {
    if (!isAuthenticated) {
      return null;
    }

    const sharedProps = { currentUser, data: appData };

    switch (activeRoute) {
      case 'dashboard':
        return <DashboardPage {...sharedProps} />;
      case 'mentors':
        return <MentorsPage {...sharedProps} />;
      case 'booking':
        return <BookingPage {...sharedProps} />;
      case 'groups':
        return <GroupsPage {...sharedProps} />;
      case 'admin':
        return role === 'ROLE_ADMIN' ? <AdminPage {...sharedProps} /> : <DashboardPage {...sharedProps} />;
      case 'skills-admin':
        return role === 'ROLE_ADMIN' ? <AdminSkillsPage {...sharedProps} /> : <DashboardPage {...sharedProps} />;
      default:
        return <DashboardPage {...sharedProps} />;
    }
  }, [activeRoute, currentUser, isAuthenticated, role]);

  const handleLogout = () => {
    dispatch(clearCredentials());
    navigate('/login', { replace: true });
  };

  const shell = (
    <AppShell
      activeRoute={activeRoute}
      currentUser={currentUser}
      navigation={navigation}
      pageTitle={pageTitles[activeRoute] || 'SkillSync'}
      role={role}
      onLogout={handleLogout}
      onRoleSwitch={() => {}}
    >
      {routeContent}
    </AppShell>
  );

  return (
    <Routes>
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate replace to={role === 'ROLE_ADMIN' ? '/admin' : '/dashboard'} /> : <LoginPage />}
      />
      <Route
        path="/signup"
        element={isAuthenticated ? <Navigate replace to={role === 'ROLE_ADMIN' ? '/admin' : '/dashboard'} /> : <SignupPage />}
      />
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<Navigate replace to={role === 'ROLE_ADMIN' ? '/admin' : '/dashboard'} />} />
        <Route path="/dashboard" element={shell} />
        <Route path="/mentors" element={shell} />
        <Route path="/booking" element={shell} />
        <Route path="/groups" element={shell} />
        <Route path="/admin" element={shell} />
        <Route path="/skills-admin" element={shell} />
      </Route>
      <Route path="*" element={<Navigate replace to={isAuthenticated ? (role === 'ROLE_ADMIN' ? '/admin' : '/dashboard') : '/login'} />} />
    </Routes>
  );
}

export default App;
