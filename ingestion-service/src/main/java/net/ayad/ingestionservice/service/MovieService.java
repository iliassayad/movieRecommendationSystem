package net.ayad.ingestionservice.service;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.dto.GenreDTO;
import net.ayad.ingestionservice.dto.MovieDTO;
import net.ayad.ingestionservice.dto.MovieDetailDTO;
import net.ayad.ingestionservice.entity.Genre;
import net.ayad.ingestionservice.entity.Movie;
import net.ayad.ingestionservice.mapper.GenreMapper;
import net.ayad.ingestionservice.mapper.MovieMapper;
import net.ayad.ingestionservice.repository.GenreRepository;
import net.ayad.ingestionservice.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;



    public MovieDTO getMovieByMovieId(Long movieId) {
        Movie movie = movieRepository.findByMovieId(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with movieId: " + movieId));
        MovieDTO movieDTO = movieMapper.toMovieDTO(movie);
        List<String> genreNames = genreRepository
                .findAllById(movie.getGenreIds())
                .stream()
                .map(Genre::getName)
                .toList();

        movieDTO.setGenreNames(genreNames);
        return movieDTO;
    }


    public Page<MovieDTO> getAllMovies(PageRequest pageRequest) {

        Page<Movie> moviePage = movieRepository.findAll(pageRequest);

        // 1️⃣ Récupérer tous les genreIds uniques
        Set<Long> allGenreIds = moviePage.getContent().stream()
                .flatMap(movie -> movie.getGenreIds().stream())
                .collect(Collectors.toSet());

        // 2️⃣ Charger tous les genres en une requête
        Map<Long, String> genreMap = genreRepository.findAllById(allGenreIds)
                .stream()
                .collect(Collectors.toMap(
                        Genre::getId,
                        Genre::getName
                ));

        // 3️⃣ Mapper Movie → MovieDTO + remplir genreNames
        return moviePage.map(movie -> {
            MovieDTO dto = movieMapper.toMovieDTO(movie);

            List<String> genreNames = movie.getGenreIds().stream()
                    .map(genreMap::get)
                    .filter(Objects::nonNull)
                    .toList();

            dto.setGenreNames(genreNames);
            return dto;
        });
    }

    public MovieDetailDTO getMovieDetailByMovieId(Long movieId) {
        Movie movie = movieRepository.findByMovieId(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with movieId: " + movieId));
        MovieDetailDTO movieDetailDTO = movieMapper.toMovieDetailDTO(movie);
        List<GenreDTO> genreDTOs = movie.getGenreIds().stream()
                .map(id -> genreRepository.findById(id)
                        .map(genreMapper::toGenreDTO)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        movieDetailDTO.setGenres(genreDTOs);
        return movieDetailDTO;
    }

    public Page<MovieDTO> getMoviesByGenre(Long genreId, PageRequest pageRequest) {
        Page<Movie> moviePage = movieRepository.findByGenreIdsContaining(genreId, pageRequest);

        return moviePage.map(movie -> {
            MovieDTO dto = movieMapper.toMovieDTO(movie);

            List<String> genreNames = movie.getGenreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .map(Genre::getName)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            dto.setGenreNames(genreNames);
            return dto;
        });
    }

    public List<MovieDTO> getPopularMovies(int limit) {
        List<Movie> popularMovies = movieRepository.findTopNByOrderByPopularityDesc(limit);
        return popularMovies.stream()
                .map(movie -> {
                    MovieDTO dto = movieMapper.toMovieDTO(movie);
                    List<String> genreNames = movie.getGenreIds().stream()
                            .map(id -> genreRepository.findById(id)
                                    .map(Genre::getName)
                                    .orElse(null))
                            .filter(Objects::nonNull)
                            .toList();
                    dto.setGenreNames(genreNames);
                    return dto;
                })
                .toList();
    }

    public Page<MovieDTO> searchMoviesByTitle(String query, PageRequest pageRequest) {
        Page<Movie> moviePage = movieRepository.findByTitleContainingIgnoreCase(query, pageRequest);

        return moviePage.map(movie -> {
            MovieDTO dto = movieMapper.toMovieDTO(movie);

            List<String> genreNames = movie.getGenreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .map(Genre::getName)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            dto.setGenreNames(genreNames);
            return dto;
        });
    }
}
