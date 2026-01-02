package net.ayad.ingestionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.dto.MovieDTO;
import net.ayad.ingestionservice.dto.MovieDetailDTO;
import net.ayad.ingestionservice.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Movie Controller", description = "APIs for managing movies")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {
    private final MovieService movieService;

   @Operation(summary = "Get All Movies", description = "Retrieve a paginated list of all movies with sorting options")
   @ApiResponse(responseCode = "200", description = "Successfully retrieved list of movies")
   @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAllMovies(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "20") int size,
           @RequestParam(defaultValue = "popularity") String sortBy,
           @RequestParam(defaultValue = "DESC") String sortDirection
   ){
       Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
               ? Sort.Direction.ASC
               : Sort.Direction.DESC;
       PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
       Page<MovieDTO> movies = movieService.getAllMovies(pageRequest);
       return ResponseEntity.ok(movies);
   }


    @Operation(summary = "Get Movie by ID", description = "Retrieve a movie by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the movie")
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovieByMovieId(@PathVariable Long movieId) {
        MovieDTO movie = movieService.getMovieByMovieId(movieId);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Get Movie Detail by ID", description = "Retrieve detailed information of a movie by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the movie details")
    @GetMapping("/detail/{movieId}")
    public ResponseEntity<MovieDetailDTO> getMovieDetailByMovieId(@PathVariable Long movieId) {
        MovieDetailDTO movieDetail = movieService.getMovieDetailByMovieId(movieId);
        return ResponseEntity.ok(movieDetail);
    }


    @Operation(summary = "Get Movies by Genre", description = "Retrieve a paginated list of movies filtered by genre ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of movies by genre")
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<Page<MovieDTO>> getMoviesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<MovieDTO> movies = movieService.getMoviesByGenre(genreId, pageRequest);
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "Get Popular Movies", description = "Retrieve a list of popular movies limited by the specified number")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of popular movies")
    @GetMapping("/popular")
    public ResponseEntity<List<MovieDTO>> getPopularMovies(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<MovieDTO> movies = movieService.getPopularMovies(limit);
        return ResponseEntity.ok(movies);
    }


    @Operation(summary = "Search Movies by Title", description = "Search for movies by title with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    @GetMapping("/search")
    public ResponseEntity<Page<MovieDTO>> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MovieDTO> movies = movieService.searchMoviesByTitle(query, pageRequest);
        return ResponseEntity.ok(movies);
    }

}
