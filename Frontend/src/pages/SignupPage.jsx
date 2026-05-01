import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { useRegisterMutation } from '../features/auth/authApi.js';

const roleOptions = [
  { value: 'ROLE_LEARNER', label: 'Learner' },
  { value: 'ROLE_MENTOR', label: 'Mentor' },
];

function SignupPage() {
  const navigate = useNavigate();
  const [register, { isLoading }] = useRegisterMutation();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'ROLE_LEARNER',
  });
  const [message, setMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const updateField = (field) => (event) => {
    setFormData((current) => ({
      ...current,
      [field]: event.target.value,
    }));
  };

  const handleSubmit = async () => {
    if (!formData.name.trim() || !formData.email.trim() || !formData.password.trim()) {
      setErrorMessage('Please fill in name, email, and password.');
      return;
    }

    try {
      setErrorMessage('');
      setMessage('');

      await register({
        name: formData.name.trim(),
        email: formData.email.trim(),
        password: formData.password,
        role: formData.role,
      }).unwrap();

      setMessage('Account created successfully. Please sign in with your new account.');

      window.setTimeout(() => {
        navigate('/login', {
          replace: true,
          state: { email: formData.email.trim() },
        });
      }, 900);
    } catch (error) {
      setErrorMessage(error?.data?.message || error?.data?.error || error?.message || 'Sign up failed.');
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
          <div>Create your account and start exploring the platform</div>
          <div>Choose your role based on how you want to use SkillSync</div>
          <div>Use a strong password that matches backend validation rules</div>
        </div>

        <div className="hero-highlight">
          <p>New to SkillSync?</p>
          <strong>Create your account here, then sign in and continue.</strong>
        </div>
      </section>

      <section className="login-panel">
        <div className="panel-card">
          <p className="eyebrow">Create account</p>
          <h1>Join SkillSync</h1>
          <p className="muted-text">
            Password must contain uppercase, lowercase, number, special character, and at least 8 characters.
          </p>

          <label className="field">
            <span>Full Name</span>
            <input
              placeholder="Enter your full name"
              type="text"
              value={formData.name}
              onChange={updateField('name')}
              onKeyDown={handleKeyDown}
            />
          </label>
          <br />

          <label className="field">
            <span>Email Address</span>
            <input
              placeholder="Enter your email address"
              type="email"
              value={formData.email}
              onChange={updateField('email')}
              onKeyDown={handleKeyDown}
            />
          </label>
          <br />

          <label className="field">
            <span>Password</span>
            <input
              placeholder="Create a strong password"
              type="password"
              value={formData.password}
              onChange={updateField('password')}
              onKeyDown={handleKeyDown}
            />
          </label>
          <br />

          <label className="field">
            <span>Role</span>
            <select value={formData.role} onChange={updateField('role')}>
              {roleOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
          <br />

          {message ? <p className="success-text">{message}</p> : null}
          {errorMessage ? <p className="muted-text">{errorMessage}</p> : null}
          <br />

          <button className="primary-button large-button" onClick={handleSubmit} type="button" disabled={isLoading}>
            {isLoading ? 'Creating Account...' : 'Create Account'}
          </button>

          <div className="auth-switch-note">
            Already have an account? <Link to="/login">Sign in</Link>
          </div>
        </div>
      </section>
    </div>
  );
}

export default SignupPage;
