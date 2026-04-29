import { Navigate, Outlet } from 'react-router-dom';
import { useSelector } from 'react-redux';

function ProtectedRoute() {
  const user = useSelector((state) => state.auth.user);

  if (!user) {
    return <Navigate replace to="/login" />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
