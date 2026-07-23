import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { auth, signOut } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    signOut();
    navigate('/login');
  };

  return (
    <header className="navbar">
      <div className="navbar-brand">🏦 Banking Application</div>
      {auth && (
        <div className="navbar-user">
          <span>{auth.username} · {auth.role.replace('_', ' ')}</span>
          <button className="btn-link" onClick={handleLogout}>Logout</button>
        </div>
      )}
    </header>
  );
}
