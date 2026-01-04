import api from './axios';

export const moviesAPI = {
  getAllMovies: async (page = 0, size = 20, sortBy = 'popularity', sortDirection = 'DESC') => {
    const response = await api.get('/api/v1/movies', {
      params: { page, size, sortBy, sortDirection },
    });
    return response.data;
  },

  getMovieById: async (movieId) => {
    const response = await api.get(`/api/v1/movies/${movieId}`);
    return response.data;
  },

  getMovieDetail: async (movieId) => {
    const response = await api.get(`/api/v1/movies/detail/${movieId}`);
    return response.data;
  },

  searchMovies: async (query, page = 0, size = 20) => {
    const response = await api.get('/api/v1/movies/search', {
      params: { query, page, size },
    });
    return response.data;
  },

  getPopularMovies: async (limit = 10) => {
    const response = await api.get('/api/v1/movies/popular', {
      params: { limit },
    });
    return response.data;
  },

  getMoviesByGenre: async (genreId, page = 0, size = 20) => {
    const response = await api.get(`/api/v1/movies/genre/${genreId}`, {
      params: { page, size },
    });
    return response.data;
  },

  rateMovie: async (movieId, rating) => {
    const response = await api.post('/api/v1/movies/rate', {
      movieId,
      rating,
    });
    return response.data;
  },
};