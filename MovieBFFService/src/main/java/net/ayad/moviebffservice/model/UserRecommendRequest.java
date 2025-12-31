package net.ayad.moviebffservice.model;

public record UserRecommendRequest(
        Long userId,
        Integer topN
) {
}
