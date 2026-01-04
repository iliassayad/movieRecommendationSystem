import api from './axios';

export const genresAPI = {
  getAllGenres: async () => {
    const response = await api.get('/api/v1/genres');
    return response.data;
  },

  getGenreById: async (id) => {
    const response = await api.get(`/api/v1/genres/${id}`);
    return response.data;
  },
};