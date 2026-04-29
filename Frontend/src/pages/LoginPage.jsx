import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { Link, useLocation, useNavigate } from 'react-router-dom';

import { useLoginMutation } from '../features/auth/authApi.js';
import { setCredentials } from '../features/auth/authSlice.js';

function LoginPage() {
  const dispatch = useDispatch();
  const location = useLocation();
  const navigate = useNavigate();
  const [email, setEmail] = useState(location.state?.email || '');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [login, { isLoading }] = useLoginMutation();

  const handleSubmit = async () => {
    if (!email.trim() || !password.trim()) {
      setErrorMessage('Please enter both email and password.');
      return;
    }

    try {
      setErrorMessage('');

      const response = await login({
          email: email.trim(),
          password,
        }).unwrap();

      dispatch(setCredentials(response));
      navigate(response.role === 'ROLE_ADMIN' ? '/admin' : '/dashboard', { replace: true });
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Login failed.');
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === 'Enter') {
      handleSubmit();
    }
  };

  return (
    <div className="login-page">
      <section className="login-hero">
        <div className="brand-mark large">
          Skill<span>Sync</span>
        </div>
        <p className="hero-subtitle">Peer learning and mentor matching platform</p>

        <div className="hero-list">
          <div>Find expert mentors matched to your goals</div>
          <div>Book 1-on-1 sessions at your convenience</div>
          <div>Join peer learning groups and communities</div>
          <div>Track your growth with ratings and reviews</div>
        </div>

        <div className="hero-highlight">
          <p>Connected workspace</p>
          <strong>Sign in with your backend account to continue.</strong>
        </div>
      </section>

      <section className="login-panel">
        <div className="panel-card">
          <p className="eyebrow">Welcome back</p>
          <h1>Sign in to your SkillSync workspace</h1>
          <p className="muted-text">
            Use the same email and password that exist in your backend database.
          </p>

          <label className="field">
            <span>Email Address</span>
            <input
              placeholder="Enter your email address"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              onKeyDown={handleKeyDown}
            />
          </label>
          <br />

          <label className="field">
            <span>Password</span>
            <input
              placeholder="Enter password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              onKeyDown={handleKeyDown}
            />
          </label>
          <br />

          {errorMessage ? <p className="muted-text">{errorMessage}</p> : null}
          <br />

          <button
            className="primary-button large-button"
            onClick={handleSubmit}
            type="button"
            disabled={isLoading}
          >
            {isLoading ? 'Signing In...' : 'Enter Dashboard'}
          </button>

          <div className="helper-note">
            This login now uses the backend API through the API gateway.
          </div>

          <div className="auth-switch-note">
            Don&apos;t have an account? <Link to="/signup">Create one</Link>
          </div>
        </div>
      </section>
    </div>
  );
}

export default LoginPage;
