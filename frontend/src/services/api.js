import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({ baseURL: API_BASE_URL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const login = (username, password) => api.post('/auth/login', { username, password });
export const createCustomer = (data) => api.post('/customers', data);
export const getCustomers = () => api.get('/customers');
export const createAccount = (data) => api.post('/accounts', data);
export const getAccounts = () => api.get('/accounts');
export const deposit = (data) => api.post('/transactions/deposit', data);
export const withdraw = (data) => api.post('/transactions/withdraw', data);
export const getTransactionHistory = (accountNo) => api.get(`/transactions/${accountNo}/history`);
export const getPendingAccounts = () => api.get('/manager/accounts/pending');
export const approveAccount = (accountNo, remarks) => api.put(`/manager/accounts/${accountNo}/approve`, { remarks });
export const rejectAccount = (accountNo, remarks) => api.put(`/manager/accounts/${accountNo}/reject`, { remarks });

export default api;
