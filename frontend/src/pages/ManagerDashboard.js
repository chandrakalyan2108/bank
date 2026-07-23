import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getPendingAccounts, approveAccount, rejectAccount } from '../services/api';

export default function ManagerDashboard() {
  const [pending, setPending] = useState([]);
  const [message, setMessage] = useState('');

  const loadPending = async () => {
    const res = await getPendingAccounts();
    setPending(res.data.data);
  };

  useEffect(() => { loadPending(); }, []);

  const notify = (msg) => { setMessage(msg); setTimeout(() => setMessage(''), 3000); };

  const handleApprove = async (accountNo) => {
    await approveAccount(accountNo, 'Approved by bank manager');
    notify(`Account ${accountNo} approved — status ACTIVE`);
    loadPending();
  };

  const handleReject = async (accountNo) => {
    await rejectAccount(accountNo, 'Rejected by bank manager');
    notify(`Account ${accountNo} rejected`);
    loadPending();
  };

  return (
    <div>
      <Navbar />
      <main className="page">
        <h2>Bank Manager Dashboard</h2>
        {message && <div className="alert-success">{message}</div>}

        <h3>Pending Accounts ({pending.length})</h3>
        <table className="table">
          <thead>
            <tr><th>Account No</th><th>Customer ID</th><th>Type</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {pending.map((a) => (
              <tr key={a.accountNo}>
                <td>{a.accountNo}</td><td>{a.customerId}</td><td>{a.accountType}</td>
                <td><span className="badge badge-pending">{a.status}</span></td>
                <td className="btn-row">
                  <button className="btn-primary" onClick={() => handleApprove(a.accountNo)}>Approve</button>
                  <button className="btn-danger" onClick={() => handleReject(a.accountNo)}>Reject</button>
                </td>
              </tr>
            ))}
            {pending.length === 0 && (
              <tr><td colSpan="5" className="empty-state">No pending accounts</td></tr>
            )}
          </tbody>
        </table>
      </main>
    </div>
  );
}
