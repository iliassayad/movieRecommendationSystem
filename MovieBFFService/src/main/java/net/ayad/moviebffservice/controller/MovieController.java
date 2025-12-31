package net.ayad.moviebffservice.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;



    @PostMapping("/recommend")
    public ResponseEntity<List<MovieDTO>> getRecommendationsForUser(@RequestBody UserRecommendRequest request) {
        List<MovieDTO> recommendedMovies = movieService.provideRecommendations(request);
        return ResponseEntity.ok(recommendedMovies);
    }
}
