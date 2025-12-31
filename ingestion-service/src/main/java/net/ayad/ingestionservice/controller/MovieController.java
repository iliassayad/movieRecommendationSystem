package net.ayad.ingestionservice.controller;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.dto.MovieDTO;
import net.ayad.ingestionservice.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingestion")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        List<MovieDTO> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovieByMovieId(@PathVariable Long movieId) {
        MovieDTO movie = movieService.getMovieByMovieId(movieId);
        return ResponseEntity.ok(movie);
    }
}
