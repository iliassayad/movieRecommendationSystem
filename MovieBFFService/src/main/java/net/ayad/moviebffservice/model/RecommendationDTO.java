package net.ayad.moviebffservice.model;

import java.util.List;


public record RecommendationDTO(
        Long userId,
        List<RecommendedMovie> recommendations
) {}

