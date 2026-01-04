// src/pages/Search.jsx
import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { moviesAPI } from '../api/movies';
import MovieCard from '../components/MovieCard';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import Pagination from '../components/Pagination';
import { Search as SearchIcon } from 'lucide-react';

const Search = () => {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (query) {
      searchMovies();
    }
  }, [query, currentPage]);

  const searchMovies = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await moviesAPI.searchMovies(query, currentPage, 20);
      setMovies(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError('Erreur lors de la recherche');
      console.error('Error searching movies:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="min-h-screen bg-gray-900">
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center gap-3 mb-8">
          <SearchIcon className="w-8 h-8 text-blue-500" />
          <h1 className="text-3xl font-bold text-white">
            Résultats pour "{query}"
          </h1>
        </div>

        {loading ? (
          <Loading message="Recherche en cours..." />
        ) : error ? (
          <ErrorMessage message={error} onRetry={searchMovies} />
        ) : movies.length === 0 ? (
          <div className="text-center py-20">
            <SearchIcon className="w-16 h-16 text-gray-600 mx-auto mb-4" />
            <p className="text-gray-400 text-lg">
              Aucun film trouvé pour "{query}"
            </p>
          </div>
        ) : (
          <>
            <p className="text-gray-400 mb-6">
              {movies.length} film{movies.length > 1 ? 's' : ''} trouvé{movies.length > 1 ? 's' : ''}
            </p>

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

export default Search;