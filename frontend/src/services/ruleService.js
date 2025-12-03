import api from "./api";

export const getRules = async () => {
  const res = await api.get("/api/rules");
  return res.data;
};

export const getRuleById = async (id) => {
  const res = await api.get(`/api/rules`);
  const all = res.data || [];
  return all.find((r) => String(r.id) === String(id));
};

export const createRule = async (rule) => {
  const res = await api.post("/api/rules", rule);
  return res.data;
};

export const updateRule = async (id, rule) => {
  const res = await api.put(`/api/rules/${id}`, rule);
  return res.data;
};

export const deleteRule = async (id) => {
  await api.delete(`/api/rules/${id}`);
};

export const reloadRules = async () => {
  const res = await api.post("/api/rules/reload");
  return res.data;
};

/**
 * Export all rules as JSON
 */
export const exportRules = async () => {
  const res = await api.get("/api/rules/export");
  return res.data;
};

/**
 * Import rules from JSON array
 * @param {Array} rules - Array of rule objects
 * @param {boolean} skipDuplicates - Skip existing rules (true) or update them (false)
 */
export const importRules = async (rules, skipDuplicates = false) => {
  const res = await api.post("/api/rules/import", rules, {
    params: { skipDuplicates }
  });
  return res.data;
};

