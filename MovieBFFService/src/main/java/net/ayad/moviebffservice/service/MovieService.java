package net.ayad.moviebffservice.service;

import lombok.RequiredArgsConstructor;
import net.ayad.moviebffservice.feign.MovieRestClient;
import net.ayad.moviebffservice.feign.RecommendationRestClient;
import net.ayad.moviebffservice.model.MovieDTO;
import net.ayad.moviebffservice.model.RecommendationDTO;
import net.ayad.moviebffservice.model.UserRecommendRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRestClient movieRestClient;
    private final RecommendationRestClient recommendationRestClient;

    private MovieDTO getMovieByMovieId(Long movieId) {
        return movieRestClient.getMovieByMovieId(movieId);
    }


    private RecommendationDTO getRecommendationsForUserFromRecommendationService(UserRecommendRequest request) {
        return recommendationRestClient.recommend(request);
    }

    public List<MovieDTO> provideRecommendations() {
        UserRecommendRequest request = new UserRecommendRequest(getCurrentUserId(), 10);
        System.out.println("Requesting recommendations for user ID: " + request.userId());
        RecommendationDTO recommendations = getRecommendationsForUserFromRecommendationService(request);
        return recommendations.recommendations().stream()
                .map(recommendation -> getMovieByMovieId(recommendation.movieId()))
                .toList();
    }


    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthenticated");
        }

        return Long.valueOf(auth.getPrincipal().toString());
    }




}
