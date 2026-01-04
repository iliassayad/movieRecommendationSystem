// src/pages/MovieDetail.jsx
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { moviesAPI } from '../api/movies';
import { useAuth } from '../contexts/AuthContext';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { 
  Star, Calendar, Clock, DollarSign, 
  TrendingUp, ArrowLeft, Heart
} from 'lucide-react';

const MovieDetail = () => {
  const { movieId } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [rating, setRating] = useState(0);
  const [submittingRating, setSubmittingRating] = useState(false);
  const [ratingSuccess, setRatingSuccess] = useState(false);

  useEffect(() => {
    loadMovieDetail();
  }, [movieId]);

  const loadMovieDetail = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await moviesAPI.getMovieDetail(movieId);
      setMovie(data);
    } catch (err) {
      setError('Erreur lors du chargement des détails du film');
      console.error('Error loading movie detail:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRating = async (value) => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    try {
      setSubmittingRating(true);
      await moviesAPI.rateMovie(movieId, value);
      setRating(value);
      setRatingSuccess(true);
      setTimeout(() => setRatingSuccess(false), 3000);
    } catch (err) {
      console.error('Error rating movie:', err);
      alert('Erreur lors de la soumission de la note');
    } finally {
      setSubmittingRating(false);
    }
  };

  if (loading) return <Loading message="Chargement des détails..." />;
  if (error) return <ErrorMessage message={error} onRetry={loadMovieDetail} />;
  if (!movie) return null;

  const backdropUrl = movie.backdropPath
    ? `https://image.tmdb.org/t/p/original${movie.backdropPath}`
    : null;

  const posterUrl = movie.posterPath
    ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
    : '/placeholder-movie.jpg';

  return (
    <div className="min-h-screen bg-gray-900">
      {/* Backdrop */}
      {backdropUrl && (
        <div className="relative h-96 overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-gray-900/70 to-transparent z-10" />
          <img
            src={backdropUrl}
            alt={movie.title}
            className="w-full h-full object-cover"
          />
        </div>
      )}

      <div className="container mx-auto px-4 -mt-64 relative z-20">
        {/* Back Button */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-white mb-6 hover:text-blue-400 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          <span>Retour</span>
        </button>

        <div className="flex flex-col md:flex-row gap-8">
          {/* Poster */}
          <div className="md:w-1/3">
            <img
              src={posterUrl}
              alt={movie.title}
              className="w-full rounded-lg shadow-2xl"
            />
          </div>

          {/* Details */}
          <div className="md:w-2/3">
            <h1 className="text-4xl font-bold text-white mb-2">
              {movie.title}
            </h1>
            
            {movie.tagline && (
              <p className="text-gray-400 italic mb-4">{movie.tagline}</p>
            )}

            {/* Stats */}
            <div className="flex flex-wrap gap-6 mb-6">
              <div className="flex items-center gap-2">
                <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                <span className="text-white font-semibold">
                  {movie.voteAverage?.toFixed(1) || 'N/A'}
                </span>
                <span className="text-gray-400">
                  ({movie.voteCount} votes)
                </span>
              </div>

              <div className="flex items-center gap-2">
                <Calendar className="w-5 h-5 text-blue-400" />
                <span className="text-white">{movie.releaseDate || 'N/A'}</span>
              </div>

              {movie.runtime && (
                <div className="flex items-center gap-2">
                  <Clock className="w-5 h-5 text-green-400" />
                  <span className="text-white">{movie.runtime} min</span>
                </div>
              )}

              <div className="flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-purple-400" />
                <span className="text-white">{movie.popularity?.toFixed(0)}</span>
              </div>
            </div>

            {/* Genres */}
            {movie.genres && movie.genres.length > 0 && (
              <div className="flex flex-wrap gap-2 mb-6">
                {movie.genres.map((genre) => (
                  <span
                    key={genre.id}
                    className="px-3 py-1 bg-blue-600 text-white rounded-full text-sm"
                  >
                    {genre.name}
                  </span>
                ))}
              </div>
            )}

            {/* Overview */}
            <div className="mb-6">
              <h2 className="text-2xl font-bold text-white mb-3">Synopsis</h2>
              <p className="text-gray-300 leading-relaxed">
                {movie.overview || 'Aucun synopsis disponible'}
              </p>
            </div>

            {/* Budget & Revenue */}
            {(movie.budget > 0 || movie.revenue > 0) && (
              <div className="grid grid-cols-2 gap-4 mb-6">
                {movie.budget > 0 && (
                  <div className="bg-gray-800 p-4 rounded-lg">
                    <div className="flex items-center gap-2 mb-2">
                      <DollarSign className="w-5 h-5 text-green-400" />
                      <span className="text-gray-400">Budget</span>
                    </div>
                    <p className="text-white font-semibold text-xl">
                      ${(movie.budget / 1000000).toFixed(1)}M
                    </p>
                  </div>
                )}

                {movie.revenue > 0 && (
                  <div className="bg-gray-800 p-4 rounded-lg">
                    <div className="flex items-center gap-2 mb-2">
                      <DollarSign className="w-5 h-5 text-yellow-400" />
                      <span className="text-gray-400">Revenue</span>
                    </div>
                    <p className="text-white font-semibold text-xl">
                      ${(movie.revenue / 1000000).toFixed(1)}M
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* Rating Section */}
            <div className="bg-gray-800 p-6 rounded-lg">
              <h3 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
                <Heart className="w-6 h-6 text-red-500" />
                Noter ce film
              </h3>

              {!isAuthenticated ? (
                <p className="text-gray-400">
                  Vous devez être connecté pour noter ce film
                </p>
              ) : (
                <>
                  <div className="flex gap-2 mb-4">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        onClick={() => handleRating(star * 2)}
                        disabled={submittingRating}
                        className="transform hover:scale-110 transition-transform disabled:opacity-50"
                      >
                        <Star
                          className={`w-10 h-10 ${
                            star * 2 <= rating
                              ? 'text-yellow-400 fill-yellow-400'
                              : 'text-gray-600'
                          }`}
                        />
                      </button>
                    ))}
                  </div>

                  {ratingSuccess && (
                    <p className="text-green-500">
                      Merci pour votre note !
                    </p>
                  )}
                </>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetail;