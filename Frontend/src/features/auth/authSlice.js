import { createSlice } from '@reduxjs/toolkit';

const roleLabels = {
  ROLE_MENTOR: 'Mentor',
  ROLE_LEARNER: 'Learner',
};

const roleHeadlines = {
  ROLE_MENTOR: 'Helping learners turn concepts into projects and interview wins.',
  ROLE_LEARNER: 'Building full-stack confidence with structured mentor support.',
};

const allowedRoles = ['ROLE_LEARNER', 'ROLE_MENTOR'];

const getInitials = (name = '') =>
  name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() || '')
    .join('');

const getStoredUser = () => {
  const accessToken = localStorage.getItem('accessToken');
  const role = localStorage.getItem('role');

  if (!accessToken || !role) {
    return null;
  }

  if (!allowedRoles.includes(role)) {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('name');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    return null;
  }

  const name = localStorage.getItem('name') || 'SkillSync User';

  return {
    userId: localStorage.getItem('userId'),
    name,
    email: localStorage.getItem('email') || '',
    role,
    accessToken,
    refreshToken: localStorage.getItem('refreshToken') || '',
    initials: getInitials(name),
    roleLabel: roleLabels[role] || roleLabels.ROLE_LEARNER,
    headline: roleHeadlines[role] || roleHeadlines.ROLE_LEARNER,
  };
};

const initialState = {
  user: getStoredUser(),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (state, action) => {
      const { userId, name, email, role, accessToken, refreshToken } = action.payload;

      if (!allowedRoles.includes(role)) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('name');
        localStorage.removeItem('email');
        localStorage.removeItem('role');
        state.user = null;
        return;
      }

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userId', String(userId));
      localStorage.setItem('name', name);
      localStorage.setItem('email', email);
      localStorage.setItem('role', role);

      state.user = {
        userId,
        name,
        email,
        role,
        accessToken,
        refreshToken,
        initials: getInitials(name),
        roleLabel: roleLabels[role] || roleLabels.ROLE_LEARNER,
        headline: roleHeadlines[role] || roleHeadlines.ROLE_LEARNER,
      };
    },
    clearCredentials: (state) => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('name');
      localStorage.removeItem('email');
      localStorage.removeItem('role');

      state.user = null;
    },
  },
});

export const { setCredentials, clearCredentials } = authSlice.actions;
export default authSlice.reducer;
