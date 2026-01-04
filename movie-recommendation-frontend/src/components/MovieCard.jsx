// src/components/MovieCard.jsx
import { Link } from 'react-router-dom';
import { Star } from 'lucide-react';

const MovieCard = ({ movie }) => {
  const posterUrl = movie.posterPath 
    ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
    : '/placeholder-movie.jpg';

  return (
    <Link 
      to={`/movies/${movie.movieId}`}
      className="group block bg-gray-800 rounded-lg overflow-hidden hover:ring-2 hover:ring-blue-500 transition-all duration-300 hover:scale-105"
    >
      <div className="relative aspect-[2/3] overflow-hidden">
        <img
          src={posterUrl}
          alt={movie.title}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
          onError={(e) => {
            e.target.src = '/placeholder-movie.jpg';
          }}
        />
        <div className="absolute top-2 right-2 bg-black/70 px-2 py-1 rounded-full flex items-center gap-1">
          <Star className="w-4 h-4 text-yellow-400 fill-yellow-400" />
          <span className="text-white text-sm font-semibold">
            {movie.voteAverage?.toFixed(1) || 'N/A'}
          </span>
        </div>
      </div>
      
      <div className="p-4">
        <h3 className="text-white font-semibold text-lg line-clamp-1 mb-2">
          {movie.title}
        </h3>
        
        <div className="flex items-center justify-between text-sm text-gray-400">
          <span>{movie.releaseDate?.split('-')[0] || 'N/A'}</span>
          {movie.genreNames && movie.genreNames.length > 0 && (
            <span className="line-clamp-1">{movie.genreNames[0]}</span>
          )}
        </div>
      </div>
    </Link>
  );
};

export default MovieCard;