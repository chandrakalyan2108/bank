import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, allowedRole }) {
  const { auth } = useAuth();

  if (!auth) return <Navigate to="/login" replace />;
  if (allowedRole && auth.role !== allowedRole) return <Navigate to="/login" replace />;

  return children;
}
