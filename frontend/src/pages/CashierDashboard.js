import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { createCustomer, createAccount, getAccounts, deposit, withdraw } from '../services/api';

export default function CashierDashboard() {
  const [customerForm, setCustomerForm] = useState({ firstName: '', lastName: '', mobile: '', email: '', aadhaar: '' });
  const [accountForm, setAccountForm] = useState({ customerId: '', accountType: 'SAVINGS' });
  const [txnForm, setTxnForm] = useState({ accountNo: '', amount: '' });
  const [accounts, setAccounts] = useState([]);
  const [message, setMessage] = useState('');

  const loadAccounts = async () => {
    try {
      const res = await getAccounts();
      setAccounts(res.data.data);
    } catch (e) { /* ignore */ }
  };

  useEffect(() => { loadAccounts(); }, []);

  const notify = (msg) => { setMessage(msg); setTimeout(() => setMessage(''), 3000); };

  const submitCustomer = async (e) => {
    e.preventDefault();
    await createCustomer(customerForm);
    notify('Customer created');
    setCustomerForm({ firstName: '', lastName: '', mobile: '', email: '', aadhaar: '' });
  };

  const submitAccount = async (e) => {
    e.preventDefault();
    await createAccount(accountForm);
    notify('Account created — status PENDING, awaiting manager approval');
    setAccountForm({ customerId: '', accountType: 'SAVINGS' });
    loadAccounts();
  };

  const submitDeposit = async (e) => {
    e.preventDefault();
    await deposit(txnForm);
    notify('Deposit successful');
    loadAccounts();
  };

  const submitWithdraw = async (e) => {
    e.preventDefault();
    await withdraw(txnForm);
    notify('Withdrawal successful');
    loadAccounts();
  };

  return (
    <div>
      <Navbar />
      <main className="page">
        <h2>Cashier Dashboard</h2>
        {message && <div className="alert-success">{message}</div>}

        <div className="grid">
          <form className="card" onSubmit={submitCustomer}>
            <h3>Create Customer</h3>
            <input placeholder="First name" value={customerForm.firstName}
              onChange={(e) => setCustomerForm({ ...customerForm, firstName: e.target.value })} required />
            <input placeholder="Last name" value={customerForm.lastName}
              onChange={(e) => setCustomerForm({ ...customerForm, lastName: e.target.value })} required />
            <input placeholder="Mobile" value={customerForm.mobile}
              onChange={(e) => setCustomerForm({ ...customerForm, mobile: e.target.value })} required />
            <input placeholder="Email" value={customerForm.email}
              onChange={(e) => setCustomerForm({ ...customerForm, email: e.target.value })} />
            <input placeholder="Aadhaar" value={customerForm.aadhaar}
              onChange={(e) => setCustomerForm({ ...customerForm, aadhaar: e.target.value })} />
            <button className="btn-primary" type="submit">Create Customer</button>
          </form>

          <form className="card" onSubmit={submitAccount}>
            <h3>Create Account</h3>
            <input placeholder="Customer ID" value={accountForm.customerId}
              onChange={(e) => setAccountForm({ ...accountForm, customerId: e.target.value })} required />
            <select value={accountForm.accountType}
              onChange={(e) => setAccountForm({ ...accountForm, accountType: e.target.value })}>
              <option value="SAVINGS">Savings</option>
              <option value="CURRENT">Current</option>
            </select>
            <button className="btn-primary" type="submit">Create Account</button>
          </form>

          <div className="card">
            <h3>Deposit / Withdraw</h3>
            <input placeholder="Account No" value={txnForm.accountNo}
              onChange={(e) => setTxnForm({ ...txnForm, accountNo: e.target.value })} required />
            <input placeholder="Amount" type="number" value={txnForm.amount}
              onChange={(e) => setTxnForm({ ...txnForm, amount: e.target.value })} required />
            <div className="btn-row">
              <button className="btn-primary" onClick={submitDeposit}>Deposit</button>
              <button className="btn-secondary" onClick={submitWithdraw}>Withdraw</button>
            </div>
          </div>
        </div>

        <h3>All Accounts</h3>
        <table className="table">
          <thead>
            <tr><th>Account No</th><th>Customer ID</th><th>Type</th><th>Balance</th><th>Status</th></tr>
          </thead>
          <tbody>
            {accounts.map((a) => (
              <tr key={a.accountNo}>
                <td>{a.accountNo}</td><td>{a.customerId}</td><td>{a.accountType}</td>
                <td>{a.balance}</td>
                <td><span className={`badge badge-${a.status.toLowerCase()}`}>{a.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}
