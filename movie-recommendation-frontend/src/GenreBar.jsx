import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useRef } from 'react';

const GenreBar = ({ genres, selectedGenre, onSelectGenre }) => {
  const containerRef = useRef(null);

  const scroll = (direction) => {
    if (!containerRef.current) return;
    const scrollAmount = 200; // distance de scroll par clic
    containerRef.current.scrollBy({
      left: direction === 'right' ? scrollAmount : -scrollAmount,
      behavior: 'smooth',
    });
  };

  return (
    <div className="bg-gray-800 border-b border-gray-700 flex items-center relative">
      {/* Flèche gauche */}
      <button
        onClick={() => scroll('left')}
        className="p-2 text-gray-300 hover:text-white transition"
      >
        <ChevronLeft className="w-6 h-6" />
      </button>

      {/* Conteneur des genres */}
      <div
        ref={containerRef}
        className="flex gap-3 overflow-hidden flex-1 px-2 py-4"
      >
        {genres.map((genre) => (
          <button
            key={genre.id}
            onClick={() => onSelectGenre(genre.id)}
            className={`
              px-4 py-2 rounded-full text-sm whitespace-nowrap transition
              ${selectedGenre === genre.id
                ? 'bg-blue-600 text-white'
                : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
              }
            `}
          >
            {genre.name}
          </button>
        ))}
      </div>

      {/* Flèche droite */}
      <button
        onClick={() => scroll('right')}
        className="p-2 text-gray-300 hover:text-white transition"
      >
        <ChevronRight className="w-6 h-6" />
      </button>
    </div>
  );
};

export default GenreBar;
