import React, { useState } from 'react';
import { Link } from 'react-router-dom';
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

  const handleClearSalaryCycles = async () => {
    setLoading(true);
    try {
      const response = await settingsApi.clearSalaryCycles();
      showMessage(`Successfully cleared ${response.deletedCount} salary cycles`, 'success');
    } catch (error) {
      showMessage('Error clearing salary cycles: ' + (error.response?.data?.message || error.message), 'error');
    } finally {
      setLoading(false);
      setShowConfirmModal(false);
    }
  };

  const handleRecalculateSalaryCycles = async () => {
    setLoading(true);
    try {
      const response = await settingsApi.recalculateSalaryCycles();
      showMessage(
        `Successfully recalculated salary cycles! Created: ${response.createdCount} cycles, Updated: ${response.updatedCount || 0} cycles`,
        'success'
      );
    } catch (error) {
      showMessage('Error recalculating salary cycles: ' + (error.response?.data?.message || error.message), 'error');
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
    } else if (confirmAction === 'clearSalaryCycles') {
      handleClearSalaryCycles();
    } else if (confirmAction === 'recalculateSalaryCycles') {
      handleRecalculateSalaryCycles();
    }
  };

  const getConfirmMessage = () => {
    switch (confirmAction) {
      case 'clearAll':
        return 'Are you sure you want to clear ALL data? This will delete all transactions, tags, rules, and salary cycles. This action cannot be undone.';
      case 'clearTransactions':
        return 'Are you sure you want to clear all transactions? This will also clear all tags. This action cannot be undone.';
      case 'clearRules':
        return 'Are you sure you want to clear all rules? This action cannot be undone.';
      case 'clearTags':
        return 'Are you sure you want to clear all tags? This action cannot be undone.';
      case 'clearSalaryCycles':
        return 'Are you sure you want to clear all salary cycles? This action cannot be undone.';
      case 'recalculateSalaryCycles':
        return 'This will recalculate all salary cycles based on salary credit transactions. Existing cycles will be updated. Do you want to continue?';
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

      {/* Category Management Section - PROMINENT */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg shadow-md p-6 mb-6 border-2 border-blue-200">
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-4">
            <div className="flex-shrink-0 w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center">
              <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
              </svg>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">Category Management</h2>
              <p className="text-gray-700 mb-3">
                Manage transaction categories, assign colors and icons. Changes reflect everywhere instantly!
              </p>
              <ul className="text-sm text-gray-600 space-y-1 mb-4">
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Add, edit, and delete categories
                </li>
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Customize colors and icons for each category
                </li>
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Enable/disable categories without deleting
                </li>
              </ul>
            </div>
          </div>
        </div>
        <Link
          to="/settings/categories"
          className="inline-flex items-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md hover:shadow-lg font-semibold"
        >
          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
          </svg>
          Manage Categories
        </Link>
      </div>

      {/* Rules Management Section - PROMINENT */}
      <div className="bg-gradient-to-r from-purple-50 to-violet-50 rounded-lg shadow-md p-6 mb-6 border-2 border-purple-200">
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-4">
            <div className="flex-shrink-0 w-12 h-12 bg-purple-500 rounded-lg flex items-center justify-center">
              <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
              </svg>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">Rules Management</h2>
              <p className="text-gray-700 mb-3">
                Create and manage automatic categorization rules. Let the system categorize transactions for you!
              </p>
              <ul className="text-sm text-gray-600 space-y-1 mb-4">
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Add, edit, and delete categorization rules
                </li>
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Pattern matching based on description
                </li>
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Automatic category assignment on upload
                </li>
                <li className="flex items-center gap-2">
                  <span className="text-green-500">✓</span>
                  Import/export rules as JSON
                </li>
              </ul>
            </div>
          </div>
        </div>
        <Link
          to="/rules"
          className="inline-flex items-center px-6 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition shadow-md hover:shadow-lg font-semibold"
        >
          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
          </svg>
          Manage Rules
        </Link>
      </div>

      {/* Salary Cycle Management Section - PROMINENT */}
      <div className="bg-gradient-to-r from-green-50 to-emerald-50 rounded-lg shadow-md p-6 mb-6 border-2 border-green-200">
        <div className="flex items-start gap-4 mb-4">
          <div className="flex-shrink-0 w-12 h-12 bg-green-500 rounded-lg flex items-center justify-center">
            <svg className="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div className="flex-1">
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Salary Cycle Management</h2>
            <p className="text-gray-700 mb-3">
              Manage salary-based monthly calculations. Track expenses and savings based on your salary credit dates.
            </p>
            <ul className="text-sm text-gray-600 space-y-1 mb-4">
              <li className="flex items-center gap-2">
                <span className="text-green-500">✓</span>
                Automatically detect salary credit transactions
              </li>
              <li className="flex items-center gap-2">
                <span className="text-green-500">✓</span>
                Calculate monthly periods from salary date to salary date
              </li>
              <li className="flex items-center gap-2">
                <span className="text-green-500">✓</span>
                Track expenses and savings per salary cycle
              </li>
              <li className="flex items-center gap-2">
                <span className="text-green-500">✓</span>
                Recalculate cycles when transactions change
              </li>
            </ul>
          </div>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            onClick={() => openConfirmModal('recalculateSalaryCycles')}
            disabled={loading}
            className="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition shadow-md hover:shadow-lg font-semibold"
          >
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            {loading && confirmAction === 'recalculateSalaryCycles' ? 'Recalculating...' : 'Recalculate Salary Cycles'}
          </button>

          <button
            onClick={() => openConfirmModal('clearSalaryCycles')}
            disabled={loading}
            className="inline-flex items-center px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition shadow-md hover:shadow-lg font-semibold"
          >
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
            {loading && confirmAction === 'clearSalaryCycles' ? 'Clearing...' : 'Clear Salary Cycles'}
          </button>
        </div>

        <div className="mt-4 p-4 bg-green-100 border border-green-300 rounded-lg">
          <p className="text-sm text-green-800">
            <strong>💡 Tip:</strong> Use "Recalculate" after uploading new transactions or if salary detection needs to be updated.
            This will scan all transactions and recreate salary cycles based on detected salary credits.
          </p>
        </div>
      </div>

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

