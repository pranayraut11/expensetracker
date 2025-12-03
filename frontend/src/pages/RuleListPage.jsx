import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getRules, deleteRule, reloadRules, updateRule, exportRules, importRules } from "../services/ruleService";
import RuleTable from "../components/RuleTable";

export default function RuleListPage() {
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [showImportModal, setShowImportModal] = useState(false);
  const [importFile, setImportFile] = useState(null);
  const navigate = useNavigate();

  const fetchRules = async () => {
    setLoading(true);
    try {
      const data = await getRules();
      setRules(data);
    } catch (e) {
      setMessage(`Failed to load rules: ${e.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRules();
  }, []);

  const onDelete = async (id) => {
    if (!confirm("Delete this rule?")) return;
    try {
      await deleteRule(id);
      setMessage("Rule deleted");
      fetchRules();
    } catch (e) {
      setMessage(`Delete failed: ${e.message}`);
    }
  };

  const onToggleEnabled = async (rule) => {
    try {
      const updated = { ...rule, enabled: !rule.enabled };
      await updateRule(rule.id, updated);
      setMessage("Rule updated");
      fetchRules();
    } catch (e) {
      setMessage(`Update failed: ${e.message}`);
    }
  };

  const onReload = async () => {
    try {
      const res = await reloadRules();
      setMessage(typeof res === "string" ? res : "Rules reloaded");
    } catch (e) {
      setMessage(`Reload failed: ${e.message}`);
    }
  };

  const onExport = async () => {
    try {
      // Call backend export API
      const exportData = await exportRules();

      // Ensure includeInTotals field is present in all rules (default to true if missing)
      const rulesWithIncludes = exportData.map(rule => ({
        ...rule,
        includeInTotals: rule.includeInTotals !== undefined ? rule.includeInTotals : true
      }));

      // Create JSON blob with formatted rules
      const jsonString = JSON.stringify(rulesWithIncludes, null, 2);
      const blob = new Blob([jsonString], { type: "application/json" });

      // Create download link
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `categorization-rules-${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      setMessage(`Exported ${rulesWithIncludes.length} rule(s) successfully`);
    } catch (e) {
      setMessage(`Export failed: ${e.message}`);
    }
  };

  const onImportFileSelected = (event) => {
    const file = event.target.files?.[0];
    if (file) {
      setImportFile(file);
      setShowImportModal(true);
    }
    // Don't reset input here, we'll do it after import
  };

  const handleImport = async (skipDuplicates) => {
    if (!importFile) return;

    const reader = new FileReader();
    reader.onload = async (e) => {
      try {
        const importedRules = JSON.parse(e.target?.result);

        if (!Array.isArray(importedRules)) {
          throw new Error("Invalid format: Expected an array of rules");
        }

        // Call backend import API
        const result = await importRules(importedRules, skipDuplicates);

        setMessage(result.message || `Import complete: ${result.successCount} imported, ${result.skippedCount} skipped, ${result.errorCount} errors`);
        fetchRules();
        setShowImportModal(false);
        setImportFile(null);

        // Reset file input
        document.getElementById('import-file-input').value = "";
      } catch (e) {
        setMessage(`Import failed: ${e.message}`);
        setShowImportModal(false);
        setImportFile(null);
        document.getElementById('import-file-input').value = "";
      }
    };

    reader.onerror = () => {
      setMessage("Failed to read file");
      setShowImportModal(false);
      setImportFile(null);
      document.getElementById('import-file-input').value = "";
    };

    reader.readAsText(importFile);
  };

  const cancelImport = () => {
    setShowImportModal(false);
    setImportFile(null);
    document.getElementById('import-file-input').value = "";
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-2xl font-semibold">Categorization Rules</h1>
          <div className="flex gap-2">
            <button className="btn btn-primary" onClick={() => navigate("/rules/new")}>
              Add Rule
            </button>
            <button className="btn btn-secondary" onClick={onReload}>
              Reload Rules
            </button>
            <button
              className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition disabled:bg-gray-300 disabled:cursor-not-allowed"
              onClick={onExport}
              disabled={rules.length === 0}
              title="Export rules as JSON"
            >
              📥 Export
            </button>
            <label className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition cursor-pointer" title="Import rules from JSON">
              📤 Import
              <input
                id="import-file-input"
                type="file"
                accept=".json"
                onChange={onImportFileSelected}
                className="hidden"
              />
            </label>
          </div>
        </div>
        {message && (
          <div className={`mb-3 p-3 rounded ${
            message.includes('failed') || message.includes('error')
              ? 'bg-red-100 text-red-800'
              : 'bg-blue-100 text-blue-800'
          }`}>
            {message}
          </div>
        )}
        {loading ? (
          <div className="text-center py-8">Loading...</div>
        ) : (
          <RuleTable rules={rules} onDelete={onDelete} onEdit={(id) => navigate(`/rules/${id}`)} onToggleEnabled={onToggleEnabled} />
        )}

        {/* Import Options Modal */}
        {showImportModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full mx-4">
              <h3 className="text-xl font-semibold mb-4">Import Rules</h3>
              <p className="text-gray-600 mb-6">
                File: <span className="font-medium">{importFile?.name}</span>
              </p>
              <p className="text-sm text-gray-500 mb-6">
                What should happen if a rule with the same name already exists?
              </p>
              <div className="space-y-3 mb-6">
                <button
                  onClick={() => handleImport(false)}
                  className="w-full px-4 py-3 bg-blue-600 text-white rounded hover:bg-blue-700 transition text-left"
                >
                  <div className="font-medium">Update Existing Rules</div>
                  <div className="text-sm text-blue-100">Replace existing rules with imported data</div>
                </button>
                <button
                  onClick={() => handleImport(true)}
                  className="w-full px-4 py-3 bg-green-600 text-white rounded hover:bg-green-700 transition text-left"
                >
                  <div className="font-medium">Skip Existing Rules</div>
                  <div className="text-sm text-green-100">Only import new rules, keep existing ones</div>
                </button>
              </div>
              <button
                onClick={cancelImport}
                className="w-full px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
              >
                Cancel
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

