import React, { useState } from 'react';
import { settingsApi } from '../services/settingsApi';

export default function SettingsPage() {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState(''); // 'success' or 'error'
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [confirmAction, setConfirmAction] = useState(null);

  const showMessage = (msg, type) => {
    setMessage(msg);
    setMessageType(type);
    setTimeout(() => {
      setMessage('');
      setMessageType('');
    }, 5000);
  };

  const handleClearAllData = async () => {
    setLoading(true);
    try {
      const response = await settingsApi.clearAllData();
      showMessage(
        `Successfully cleared: ${response.deletedCounts.transactions} transactions, ${response.deletedCounts.tags} tags, ${response.deletedCounts.rules} rules`,
        'success'
      );
    } catch (error) {
      showMessage('Error clearing all data: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
      setShowConfirmModal(false);
    }
  };

  const handleClearTransactions = async () => {
    setLoading(true);
    try {
      const response = await settingsApi.clearTransactions();
      showMessage(`Successfully cleared ${response.deletedCount} transactions`, 'success');
    } catch (error) {
      showMessage('Error clearing transactions: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
      setShowConfirmModal(false);
    }
  };

  const handleClearRules = async () => {
    setLoading(true);
    try {
      const response = await settingsApi.clearRules();
      showMessage(`Successfully cleared ${response.deletedCount} rules`, 'success');
    } catch (error) {
      showMessage('Error clearing rules: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
      setShowConfirmModal(false);
    }
  };

  const handleClearTags = async () => {
    setLoading(true);
    try {
      const response = await settingsApi.clearTags();
      showMessage(`Successfully cleared ${response.deletedCount} tags`, 'success');
    } catch (error) {
      showMessage('Error clearing tags: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
      setShowConfirmModal(false);
    }
  };

  const openConfirmModal = (action) => {
    setConfirmAction(action);
    setShowConfirmModal(true);
  };

  const executeAction = () => {
    if (confirmAction === 'clearAll') {
      handleClearAllData();
    } else if (confirmAction === 'clearTransactions') {
      handleClearTransactions();
    } else if (confirmAction === 'clearRules') {
      handleClearRules();
    } else if (confirmAction === 'clearTags') {
      handleClearTags();
    }
  };

  const getConfirmMessage = () => {
    switch (confirmAction) {
      case 'clearAll':
        return 'Are you sure you want to clear ALL data? This will delete all transactions, tags, and rules. This action cannot be undone.';
      case 'clearTransactions':
        return 'Are you sure you want to clear all transactions? This will also clear all tags. This action cannot be undone.';
      case 'clearRules':
        return 'Are you sure you want to clear all rules? This action cannot be undone.';
      case 'clearTags':
        return 'Are you sure you want to clear all tags? This action cannot be undone.';
      default:
        return '';
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Settings</h1>

      {/* Message Alert */}
      {message && (
        <div
          className={`mb-6 p-4 rounded-lg ${
            messageType === 'success'
              ? 'bg-green-100 text-green-800 border border-green-300'
              : 'bg-red-100 text-red-800 border border-red-300'
          }`}
        >
          {message}
        </div>
      )}

      {/* Data Management Section */}
      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Data Management</h2>
        <p className="text-gray-600 mb-6">
          Warning: These actions will permanently delete data from your system. Please use with caution.
        </p>

        <div className="space-y-4">
          {/* Clear All Data */}
          <div className="flex items-center justify-between p-4 border border-red-200 rounded-lg bg-red-50">
            <div>
              <h3 className="font-semibold text-gray-800">Clear All Data</h3>
              <p className="text-sm text-gray-600">Delete all transactions, tags, and rules</p>
            </div>
            <button
              onClick={() => openConfirmModal('clearAll')}
              disabled={loading}
              className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
            >
              {loading && confirmAction === 'clearAll' ? 'Clearing...' : 'Clear All'}
            </button>
          </div>

          {/* Clear Transactions */}
          <div className="flex items-center justify-between p-4 border border-orange-200 rounded-lg bg-orange-50">
            <div>
              <h3 className="font-semibold text-gray-800">Clear Transactions</h3>
              <p className="text-sm text-gray-600">Delete all transactions and tags</p>
            </div>
            <button
              onClick={() => openConfirmModal('clearTransactions')}
              disabled={loading}
              className="px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
            >
              {loading && confirmAction === 'clearTransactions' ? 'Clearing...' : 'Clear Transactions'}
            </button>
          </div>

          {/* Clear Rules */}
          <div className="flex items-center justify-between p-4 border border-yellow-200 rounded-lg bg-yellow-50">
            <div>
              <h3 className="font-semibold text-gray-800">Clear Rules</h3>
              <p className="text-sm text-gray-600">Delete all categorization rules</p>
            </div>
            <button
              onClick={() => openConfirmModal('clearRules')}
              disabled={loading}
              className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
            >
              {loading && confirmAction === 'clearRules' ? 'Clearing...' : 'Clear Rules'}
            </button>
          </div>

          {/* Clear Tags */}
          <div className="flex items-center justify-between p-4 border border-blue-200 rounded-lg bg-blue-50">
            <div>
              <h3 className="font-semibold text-gray-800">Clear Tags</h3>
              <p className="text-sm text-gray-600">Delete all extracted tags</p>
            </div>
            <button
              onClick={() => openConfirmModal('clearTags')}
              disabled={loading}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
            >
              {loading && confirmAction === 'clearTags' ? 'Clearing...' : 'Clear Tags'}
            </button>
          </div>
        </div>
      </div>

      {/* Confirmation Modal */}
      {showConfirmModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full mx-4">
            <h3 className="text-xl font-semibold text-gray-800 mb-4">Confirm Action</h3>
            <p className="text-gray-600 mb-6">{getConfirmMessage()}</p>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowConfirmModal(false)}
                disabled={loading}
                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 disabled:bg-gray-100 disabled:cursor-not-allowed transition"
              >
                Cancel
              </button>
              <button
                onClick={executeAction}
                disabled={loading}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition"
              >
                {loading ? 'Processing...' : 'Confirm'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

