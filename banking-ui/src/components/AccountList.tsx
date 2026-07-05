import type { Account } from '../api/types';
import { ToastContainer, toast } from 'react-toastify';

type AccountListProps = {
  accounts: Account[];
  loading: boolean;
  error: string | null;
};

const showToastMessage = (message: string, type: 'success' | 'error') => {
  if (type === 'success') {
    toast.success(message, {
      position: "top-right",
      autoClose: 5000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
    });
  } else {
    toast.error(message, {
      position: "top-right",
      autoClose: 5000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
    });
  }
};

export function AccountList({ accounts, loading, error }: AccountListProps) {
  if (loading) {
    return <p className="status-message">Loading accounts...</p>;
  }

  if (error) {
    return <p className="error-message">Error loading accounts: {error}</p>;
    //showToastMessage(`Error loading accounts: ${error}`, 'error');
  }

  if (accounts.length === 0) {
    return <p className="status-message">No accounts found.</p>;
    //showToastMessage('No accounts found.', 'error');
  }

  return (
    <>
      <section className="account-list">
        <h2>Your Accounts</h2>
        <table>
          <thead>
            <tr>
              <th>Account</th>
              <th>Type</th>
              <th>Balance</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map((account) => (
              <tr key={account.accountNumber}>
                <td>{account.accountNumber}</td>
                <td>{account.accountType}</td>
                <td>${account.balance.toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
      <ToastContainer />
    </>
  );
}