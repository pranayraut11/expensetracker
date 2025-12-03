import axios from "axios";

// Use environment variable for API base URL
// In development: http://localhost:8080
// In production (Docker): empty string (uses nginx proxy on same domain)
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "",
});

export default api;

