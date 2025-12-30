import axios from 'axios';

const API_URL = '/settings';

export const settingsApi = {
  clearAllData: async () => {
    const response = await axios.delete(`${API_URL}/clear-all-data`);
    return response.data;
  },

  clearTransactions: async () => {
    const response = await axios.delete(`${API_URL}/clear-transactions`);
    return response.data;
  },

  clearRules: async () => {
    const response = await axios.delete(`${API_URL}/clear-rules`);
    return response.data;
  },

  clearTags: async () => {
    const response = await axios.delete(`${API_URL}/clear-tags`);
    return response.data;
  },

  clearSalaryCycles: async () => {
    const response = await axios.delete(`${API_URL}/clear-salary-cycles`);
    return response.data;
  },

  recalculateSalaryCycles: async () => {
    const response = await axios.post(`${API_URL}/recalculate-salary-cycles`);
    return response.data;
  },
};

