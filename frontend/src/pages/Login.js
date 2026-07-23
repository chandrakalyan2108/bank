import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { signIn } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(username, password);
      const { token, username: uname, role } = res.data.data;
      signIn(token, uname, role);
      navigate(role === 'BANK_MANAGER' ? '/manager' : '/cashier');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>🏦 Banking Application</h1>
        <p className="subtitle">Login with Cashier or Bank Manager credentials</p>

        {error && <div className="alert-error">{error}</div>}

        <label>Username</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} required />

        <label>Password</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Signing in...' : 'Login'}
        </button>
      </form>
    </div>
  );
}
