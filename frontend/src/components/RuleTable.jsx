export default function RuleTable({ rules = [], onEdit, onDelete, onToggleEnabled }) {
  return (
    <div className="overflow-x-auto bg-white shadow rounded">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-600">Rule Name</th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-600">Category</th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-600">Pattern</th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-600">Priority</th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-600">Enabled</th>
            <th className="px-4 py-2 text-left text-xs font-medium text-gray-600">In Totals</th>
            <th className="px-4 py-2"></th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">
          {rules.map((r) => (
            <tr key={r.id}>
              <td className="px-4 py-2">{r.ruleName}</td>
              <td className="px-4 py-2">{r.categoryName}</td>
              <td className="px-4 py-2 font-mono text-sm text-gray-700">{r.pattern}</td>
              <td className="px-4 py-2">{r.priority}</td>
              <td className="px-4 py-2">
                <label className="inline-flex items-center cursor-pointer">
                  <input type="checkbox" className="sr-only peer" checked={!!r.enabled} onChange={() => onToggleEnabled(r)} />
                  <div className="w-10 h-5 bg-gray-300 peer-checked:bg-green-500 rounded-full relative transition">
                    <div className="absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full peer-checked:left-5 transition" />
                  </div>
                </label>
              </td>
              <td className="px-4 py-2">
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                  r.includeInTotals !== false ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                }`}>
                  {r.includeInTotals !== false ? 'Yes' : 'No'}
                </span>
              </td>
              <td className="px-4 py-2 text-right">
                <div className="flex gap-2 justify-end">
                  <button className="btn btn-sm btn-secondary" onClick={() => onEdit(r.id)}>Edit</button>
                  <button className="btn btn-sm btn-danger" onClick={() => onDelete(r.id)}>Delete</button>
                </div>
              </td>
            </tr>
          ))}
          {rules.length === 0 && (
            <tr>
              <td colSpan="7" className="px-4 py-6 text-center text-gray-500">No rules found</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

