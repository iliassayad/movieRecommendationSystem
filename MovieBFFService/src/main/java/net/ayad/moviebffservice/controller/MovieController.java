package net.ayad.moviebffservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ayad.moviebffservice.model.MovieDTO;
import net.ayad.moviebffservice.model.RecommendationDTO;
import net.ayad.moviebffservice.model.UserRecommendRequest;
import net.ayad.moviebffservice.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Movie Recommendation Controller", description = "APIs for movie recommendations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
public class MovieController {

    private final MovieService movieService;



    @Operation(summary = "Get Movie Recommendations for User", description = "Retrieve a list of recommended movies for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of recommended movies")
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getRecommendationsForUser() {
        List<MovieDTO> recommendedMovies = movieService.provideRecommendations();
        return ResponseEntity.ok(recommendedMovies);
    }
}
