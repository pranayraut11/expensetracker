import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createRule, getRuleById, updateRule } from "../services/ruleService";
import RuleForm from "../components/RuleForm";

export default function RuleFormPage({ mode = "create" }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const [initial, setInitial] = useState({
    ruleName: "",
    categoryName: "Food",
    pattern: "",
    priority: 0,
    enabled: true,
    includeInTotals: true,
  });
  const [message, setMessage] = useState("");

  useEffect(() => {
    const load = async () => {
      if (mode === "edit" && id) {
        try {
          const rule = await getRuleById(id);
          if (rule) setInitial(rule);
          else setMessage("Rule not found");
        } catch (e) {
          setMessage(`Load failed: ${e.message}`);
        }
      }
    };
    load();
  }, [mode, id]);

  const onSubmit = async (values) => {
    try {
      if (mode === "create") {
        await createRule(values);
        setMessage("Rule created");
      } else {
        await updateRule(id, values);
        setMessage("Rule updated");
      }
      navigate("/rules");
    } catch (e) {
      setMessage(`Save failed: ${e.message}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-3xl mx-auto">
        <h1 className="text-2xl font-semibold mb-4">{mode === "create" ? "Add Rule" : "Edit Rule"}</h1>
        {message && <div className="mb-3 text-sm text-blue-700">{message}</div>}
        <RuleForm initialValues={initial} onSubmit={onSubmit} onCancel={() => navigate("/rules")} />
      </div>
    </div>
  );
}

