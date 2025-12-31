package net.ayad.ingestionservice.service;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.dto.MovieDTO;
import net.ayad.ingestionservice.entity.Movie;
import net.ayad.ingestionservice.mapper.MovieMapper;
import net.ayad.ingestionservice.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::toMovieDTO)
                .toList();
    }

    public MovieDTO getMovieByMovieId(Long movieId) {
        Movie movie = movieRepository.findByMovieId(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with movieId: " + movieId));
        return movieMapper.toMovieDTO(movie);
    }

}
