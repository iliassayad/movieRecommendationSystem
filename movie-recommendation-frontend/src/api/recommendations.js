import api from './axios';

export const recommendationsAPI = {
  getRecommendations: async () => {
    const response = await api.get('/api/v1/recommendations');
    return response.data;
  },
};