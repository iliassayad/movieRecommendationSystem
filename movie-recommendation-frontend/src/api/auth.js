import api from './axios';

export const authAPI = {
  login: async (username, password) => {
    const response = await api.post('/api/v1/auth/login', {
      username,
      password,
    });
    return response.data;
  },

  register: async (username, password, role = 'USER') => {
    const response = await api.post('/api/v1/auth/register', {
      username,
      password,
      role,
    });
    return response.data;
  },

  validateToken: async (token) => {
    const response = await api.get(`/api/v1/auth/validate?token=${token}`);
    return response.data;
  },
};