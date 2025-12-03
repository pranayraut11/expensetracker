import React from "react";
import { getTags } from "../services/transactionApi";
import { CATEGORIES } from "../constants/categories";

const regexExamples = [
  { label: "Exact match (case insensitive)", example: "swiggy" },
  { label: "Multiple options (OR)", example: "(swiggy|zomato|uber)" },
  { label: "Contains word", example: ".*amazon.*" },
  { label: "Starts with", example: "^upi.*" },
  { label: "Ends with", example: ".*salary$" },
  { label: "Any digit", example: "\\d+" },
];

export default function RuleForm({ initialValues, onSubmit, onCancel }) {
  const [form, setForm] = React.useState(initialValues);
  const [tags, setTags] = React.useState([]);
  const [showExamples, setShowExamples] = React.useState(false);
  const [tagSearch, setTagSearch] = React.useState("");
  const [isLoadingTags, setIsLoadingTags] = React.useState(false);

  React.useEffect(() => {
    setForm(initialValues);
  }, [initialValues]);

  // Load tags with debouncing for search
  React.useEffect(() => {
    const loadTags = async () => {
      setIsLoadingTags(true);
      try {
        const data = await getTags(100, tagSearch);
        setTags(data);
      } catch (e) {
        console.warn("Tag load failed", e);
      } finally {
        setIsLoadingTags(false);
      }
    };

    // Debounce search - wait 300ms after user stops typing
    const timeoutId = setTimeout(() => {
      loadTags();
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [tagSearch]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
  };

  const appendTag = (tag) => {
    setForm((prev) => ({
      ...prev,
      pattern: prev.pattern ? `${prev.pattern}|${tag}` : tag,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div>
        <label className="block text-sm font-medium text-gray-700">Rule Name</label>
        <input
          type="text"
          name="ruleName"
          value={form.ruleName || ""}
          onChange={handleChange}
          required
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border"
          placeholder="e.g., Food_Swiggy"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">Category</label>
        <select
          name="categoryName"
          value={form.categoryName || ""}
          onChange={handleChange}
          required
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border"
        >
          <option value="">-- Select Category --</option>
          {CATEGORIES.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">Pattern (Regex)</label>
        <input
          type="text"
          name="pattern"
          value={form.pattern || ""}
          onChange={handleChange}
          required
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border font-mono"
          placeholder="e.g., (swiggy|zomato)"
        />
        <p className="mt-1 text-xs text-gray-500">
          Java regex pattern to match transaction descriptions (case insensitive)
        </p>
      </div>

      {/* Tag Suggestions */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Quick Add Tags (from your transactions)
        </label>
        <input
          type="text"
          value={tagSearch}
          onChange={(e) => setTagSearch(e.target.value)}
          placeholder="Search tags..."
          className="mb-3 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border"
        />
        {isLoadingTags ? (
          <div className="flex justify-center py-4">
            <div className="text-sm text-gray-500">Loading tags...</div>
          </div>
        ) : tags.length > 0 ? (
          <div className="flex flex-wrap gap-2">
            {tags.slice(0, 15).map((tagObj) => (
              <button
                key={tagObj.tag}
                type="button"
                onClick={() => appendTag(tagObj.tag)}
                className="inline-flex items-center px-2.5 py-1.5 border border-gray-300 shadow-sm text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                {tagObj.tag} <span className="ml-1 text-gray-500">({tagObj.count})</span>
              </button>
            ))}
          </div>
        ) : tagSearch ? (
          <p className="text-sm text-gray-500 mt-2">No tags found matching "{tagSearch}"</p>
        ) : (
          <p className="text-sm text-gray-500 mt-2">Start typing to search tags</p>
        )}
      </div>

      {/* Regex Examples */}
      <div>
        <button
          type="button"
          onClick={() => setShowExamples(!showExamples)}
          className="text-sm text-indigo-600 hover:text-indigo-800"
        >
          {showExamples ? "Hide" : "Show"} Regex Examples
        </button>
        {showExamples && (
          <div className="mt-2 bg-gray-50 rounded-md p-4 text-sm">
            <h4 className="font-medium text-gray-900 mb-2">Pattern Examples:</h4>
            <ul className="space-y-1 text-gray-700">
              {regexExamples.map((ex, i) => (
                <li key={i}>
                  <strong>{ex.label}:</strong> <code className="bg-white px-1 rounded">{ex.example}</code>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">Priority</label>
        <input
          type="number"
          name="priority"
          value={form.priority || 10}
          onChange={handleChange}
          required
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border"
          min="1"
          max="100"
        />
        <p className="mt-1 text-xs text-gray-500">
          Higher priority rules are evaluated first
        </p>
      </div>

      <div className="flex items-center">
        <input
          type="checkbox"
          name="enabled"
          checked={form.enabled || false}
          onChange={handleChange}
          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
        />
        <label className="ml-2 block text-sm text-gray-900">Enabled</label>
      </div>

      <div className="flex items-center">
        <input
          type="checkbox"
          name="includeInTotals"
          checked={form.includeInTotals !== undefined ? form.includeInTotals : true}
          onChange={handleChange}
          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
        />
        <label className="ml-2 block text-sm text-gray-900">Include in Totals</label>
        <span className="ml-2 text-xs text-gray-500">
          (Uncheck to exclude matching transactions from dashboard totals)
        </span>
      </div>

      <div className="flex justify-end space-x-3 pt-4 border-t">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Cancel
        </button>
        <button
          type="submit"
          className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Save Rule
        </button>
</div>
    </form>
  );
}