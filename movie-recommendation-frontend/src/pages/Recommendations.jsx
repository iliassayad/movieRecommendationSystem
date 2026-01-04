// src/pages/Recommendations.jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { recommendationsAPI } from '../api/recommendations';
import { useAuth } from '../contexts/AuthContext';
import MovieCard from '../components/MovieCard';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { Sparkles, Info } from 'lucide-react';

const Recommendations = () => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    loadRecommendations();
  }, [isAuthenticated, navigate]);

  const loadRecommendations = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await recommendationsAPI.getRecommendations();
      setRecommendations(data);
    } catch (err) {
      setError('Erreur lors du chargement des recommandations');
      console.error('Error loading recommendations:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-900">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            <Sparkles className="w-8 h-8 text-purple-500" />
            <h1 className="text-3xl font-bold text-white">
              Recommandations Personnalisées
            </h1>
          </div>

          <div className="bg-blue-900/30 border border-blue-500/50 rounded-lg p-4 flex items-start gap-3">
            <Info className="w-5 h-5 text-blue-400 flex-shrink-0 mt-0.5" />
            <p className="text-gray-300 text-sm">
              Ces recommandations sont basées sur vos précédentes notes et préférences.
              Notez plus de films pour améliorer la précision des suggestions !
            </p>
          </div>
        </div>

        {/* Content */}
        {loading ? (
          <Loading message="Chargement de vos recommandations..." />
        ) : error ? (
          <ErrorMessage message={error} onRetry={loadRecommendations} />
        ) : recommendations.length === 0 ? (
          <div className="text-center py-20">
            <Sparkles className="w-16 h-16 text-gray-600 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-white mb-2">
              Pas encore de recommandations
            </h3>
            <p className="text-gray-400 mb-6">
              Notez quelques films pour recevoir des recommandations personnalisées
            </p>
            <button
              onClick={() => navigate('/')}
              className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Découvrir des films
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            {recommendations.map((movie) => (
              <MovieCard key={movie.movieId} movie={movie} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Recommendations;