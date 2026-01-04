// src/pages/Home.jsx
import { useState, useEffect } from 'react';
import { moviesAPI } from '../api/movies';
import { genresAPI } from '../api/genres';
import MovieCard from '../components/MovieCard';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import Pagination from '../components/Pagination';
import { TrendingUp, Filter } from 'lucide-react';

const Home = () => {
  const [movies, setMovies] = useState([]);
  const [popularMovies, setPopularMovies] = useState([]);
  const [genres, setGenres] = useState([]);
  const [selectedGenre, setSelectedGenre] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState('popularity');

  useEffect(() => {
    loadGenres();
    loadPopularMovies();
  }, []);

  useEffect(() => {
    loadMovies();
  }, [currentPage, selectedGenre, sortBy]);

  const loadGenres = async () => {
    try {
      const data = await genresAPI.getAllGenres();
      setGenres(data);
    } catch (err) {
      console.error('Error loading genres:', err);
    }
  };

  const loadPopularMovies = async () => {
    try {
      const data = await moviesAPI.getPopularMovies(10);
      setPopularMovies(data);
    } catch (err) {
      console.error('Error loading popular movies:', err);
    }
  };

  const loadMovies = async () => {
    try {
      setLoading(true);
      setError(null);

      let data;
      if (selectedGenre) {
        data = await moviesAPI.getMoviesByGenre(selectedGenre, currentPage, 20);
      } else {
        data = await moviesAPI.getAllMovies(currentPage, 20, sortBy, 'DESC');
      }

      setMovies(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError('Erreur lors du chargement des films');
      console.error('Error loading movies:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleGenreChange = (genreId) => {
    setSelectedGenre(genreId);
    setCurrentPage(0);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="min-h-screen bg-gray-900">
      {/* Hero Section - Popular Movies */}
      <div className="bg-gradient-to-b from-blue-900 to-gray-900 py-12">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-3 mb-6">
            <TrendingUp className="w-8 h-8 text-blue-500" />
            <h2 className="text-3xl font-bold text-white">Films Populaires</h2>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-5 gap-4 overflow-x-auto">
            {popularMovies.map((movie) => (
              <MovieCard key={movie.movieId} movie={movie} />
            ))}
          </div>
        </div>
      </div>

      {/* Filters and Movies List */}
      <div className="container mx-auto px-4 py-8">
        {/* Filters */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            <Filter className="w-6 h-6 text-gray-400" />
            <h3 className="text-xl font-semibold text-white">Filtres</h3>
          </div>

          <div className="flex flex-wrap gap-4">
            {/* Genre Filter */}
            <div className="flex-1 min-w-[200px]">
              <label className="block text-gray-400 mb-2">Genre</label>
              <select
                value={selectedGenre || ''}
                onChange={(e) => handleGenreChange(e.target.value || null)}
                className="w-full bg-gray-800 text-white px-4 py-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Tous les genres</option>
                {genres.map((genre) => (
                  <option key={genre.id} value={genre.id}>
                    {genre.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Sort By */}
            <div className="flex-1 min-w-[200px]">
              <label className="block text-gray-400 mb-2">Trier par</label>
              <select
                value={sortBy}
                onChange={(e) => {
                  setSortBy(e.target.value);
                  setCurrentPage(0);
                }}
                className="w-full bg-gray-800 text-white px-4 py-2 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="popularity">Popularité</option>
                <option value="voteAverage">Note moyenne</option>
                <option value="releaseDate">Date de sortie</option>
                <option value="title">Titre</option>
              </select>
            </div>
          </div>
        </div>

        {/* Movies Grid */}
        {loading ? (
          <Loading message="Chargement des films..." />
        ) : error ? (
          <ErrorMessage message={error} onRetry={loadMovies} />
        ) : movies.length === 0 ? (
          <div className="text-center py-20">
            <p className="text-gray-400 text-lg">Aucun film trouvé</p>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
              {movies.map((movie) => (
                <MovieCard key={movie.movieId} movie={movie} />
              ))}
            </div>

            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </>
        )}
      </div>
    </div>
  );
};

export default Home;