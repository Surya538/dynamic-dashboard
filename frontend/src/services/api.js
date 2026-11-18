import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to inject JWT Bearer Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for handling 401 Unauthorized or RFC 7807 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Optional: auto logout or clear invalid token
      console.warn('Session expired or unauthorized. Please re-login.');
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (data) => api.post('/auth/register', data),
};

export const workspaceApi = {
  getAll: () => api.get('/workspaces'),
  getById: (id) => api.get(`/workspaces/${id}`),
  create: (data) => api.post('/workspaces', data),
  update: (id, data) => api.put(`/workspaces/${id}`, data),
  delete: (id) => api.delete(`/workspaces/${id}`),
};

export const dashboardApi = {
  getByWorkspace: (workspaceId) => api.get(`/dashboards/workspace/${workspaceId}`),
  getById: (id) => api.get(`/dashboards/${id}`),
  create: (data) => api.post('/dashboards', data),
  update: (id, data) => api.put(`/dashboards/${id}`, data),
  delete: (id) => api.delete(`/dashboards/${id}`),
};

export const widgetApi = {
  getByDashboard: (dashboardId) => api.get(`/widgets/dashboard/${dashboardId}`),
  create: (data) => api.post('/widgets', data),
  update: (id, data) => api.put(`/widgets/${id}`, data),
  delete: (id) => api.delete(`/widgets/${id}`),
};

export const metricApi = {
  aggregate: (query) => api.post('/metrics/aggregate', query),
  getSchema: () => api.get('/metrics/schema'),
};

export const datasetApi = {
  addRecord: (record) => api.post('/datasets/records', record),
  getRecords: (workspaceId, datasetName) => api.get(`/datasets/workspace/${workspaceId}?datasetName=${datasetName}`),
};

export const exportApi = {
  excel: (data) => api.post('/export/excel', data, { responseType: 'blob' }),
  csv: (data) => api.post('/export/csv', data, { responseType: 'blob' }),
};

export default api;
