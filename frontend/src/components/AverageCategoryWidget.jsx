import React, { useState, useEffect } from 'react';
import { getAverageCategoryData } from '../services/averageCategoryApi';

const AverageCategoryWidget = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedMonths, setSelectedMonths] = useState(12);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [viewMode, setViewMode] = useState('both'); // 'both', 'income', 'expense'

  useEffect(() => {
    fetchData();
  }, [selectedMonths, selectedYear]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const result = await getAverageCategoryData(selectedYear, selectedMonths);
      setData(result);
    } catch (error) {
      console.error('Error fetching average category data:', error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return `₹${Math.abs(amount).toLocaleString('en-IN', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    })}`;
  };

  const getCategoryColor = (category) => {
    const colors = {
      Food: 'bg-orange-100 border-orange-300',
      Groceries: 'bg-green-100 border-green-300',
      Shopping: 'bg-purple-100 border-purple-300',
      Travel: 'bg-blue-100 border-blue-300',
      Income: 'bg-emerald-100 border-emerald-300',
      Bills: 'bg-yellow-100 border-yellow-300',
      Fuel: 'bg-red-100 border-red-300',
      Medical: 'bg-pink-100 border-pink-300',
      Rent: 'bg-indigo-100 border-indigo-300',
      Entertainment: 'bg-fuchsia-100 border-fuchsia-300',
      Insurance: 'bg-cyan-100 border-cyan-300',
      Investment: 'bg-teal-100 border-teal-300',
      Education: 'bg-violet-100 border-violet-300',
    };
    return colors[category] || 'bg-gray-100 border-gray-300';
  };

  const filterData = () => {
    if (viewMode === 'income') {
      return data.filter(item => item.totalIncome > 0);
    } else if (viewMode === 'expense') {
      return data.filter(item => item.totalExpense > 0);
    }
    return data;
  };

  const filteredData = filterData();

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-center items-center h-64">
          <div className="text-gray-500">Loading average category data...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex flex-col md:flex-row md:justify-between md:items-center mb-6 gap-4">
        <h3 className="text-lg font-semibold text-gray-800">
          Average Monthly Income & Expense by Category
        </h3>

        <div className="flex flex-wrap gap-2">
          {/* Time Period Selector */}
          <select
            value={selectedMonths}
            onChange={(e) => setSelectedMonths(Number(e.target.value))}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value={3}>Last 3 Months</option>
            <option value={6}>Last 6 Months</option>
            <option value={12}>Last 12 Months</option>
          </select>

          {/* Year Selector */}
          <select
            value={selectedYear}
            onChange={(e) => setSelectedYear(Number(e.target.value))}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            {[...Array(5)].map((_, i) => {
              const year = new Date().getFullYear() - i;
              return (
                <option key={year} value={year}>
                  {year}
                </option>
              );
            })}
          </select>

          {/* View Mode Selector */}
          <select
            value={viewMode}
            onChange={(e) => setViewMode(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="both">Income & Expense</option>
            <option value="income">Income Only</option>
            <option value="expense">Expense Only</option>
          </select>
        </div>
      </div>

      {filteredData.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          No data available for the selected period
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead className="bg-gray-50 border-b-2 border-gray-200">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                  Category
                </th>
                {(viewMode === 'both' || viewMode === 'income') && (
                  <>
                    <th className="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                      Total Income
                    </th>
                    <th className="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                      Avg/Month
                    </th>
                  </>
                )}
                {(viewMode === 'both' || viewMode === 'expense') && (
                  <>
                    <th className="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                      Total Expense
                    </th>
                    <th className="px-4 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                      Avg/Month
                    </th>
                  </>
                )}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredData.map((item, index) => (
                <tr key={index} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-3">
                    <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium border ${getCategoryColor(item.category)}`}>
                      {item.category}
                    </span>
                  </td>
                  {(viewMode === 'both' || viewMode === 'income') && (
                    <>
                      <td className="px-4 py-3 text-right text-sm text-green-700 font-medium">
                        {item.totalIncome > 0 ? formatCurrency(item.totalIncome) : '—'}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <div className="flex flex-col items-end">
                          <span className="text-sm font-semibold text-green-600">
                            {item.averageMonthlyIncome > 0 ? formatCurrency(item.averageMonthlyIncome) : '—'}
                          </span>
                          {item.averageMonthlyIncome > 0 && (
                            <span className="text-xs text-gray-500">
                              per month
                            </span>
                          )}
                        </div>
                      </td>
                    </>
                  )}
                  {(viewMode === 'both' || viewMode === 'expense') && (
                    <>
                      <td className="px-4 py-3 text-right text-sm text-red-700 font-medium">
                        {item.totalExpense > 0 ? formatCurrency(item.totalExpense) : '—'}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <div className="flex flex-col items-end">
                          <span className="text-sm font-semibold text-red-600">
                            {item.averageMonthlyExpense > 0 ? formatCurrency(item.averageMonthlyExpense) : '—'}
                          </span>
                          {item.averageMonthlyExpense > 0 && (
                            <span className="text-xs text-gray-500">
                              per month
                            </span>
                          )}
                        </div>
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Summary Footer */}
      {filteredData.length > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <div className="flex justify-between text-sm text-gray-600">
            <span>Analyzing {selectedMonths} months of data</span>
            <span className="font-medium">{filteredData.length} categories</span>
          </div>
        </div>
      )}
    </div>
  );
};

export default AverageCategoryWidget;

