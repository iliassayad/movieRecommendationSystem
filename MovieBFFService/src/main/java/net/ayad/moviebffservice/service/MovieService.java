package net.ayad.moviebffservice.service;

import lombok.RequiredArgsConstructor;
import net.ayad.moviebffservice.feign.MovieRestClient;
import net.ayad.moviebffservice.feign.RecommendationRestClient;
import net.ayad.moviebffservice.model.MovieDTO;
import net.ayad.moviebffservice.model.RecommendationDTO;
import net.ayad.moviebffservice.model.UserRecommendRequest;
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

    public List<MovieDTO> provideRecommendations(UserRecommendRequest request) {
        RecommendationDTO recommendations = getRecommendationsForUserFromRecommendationService(request);
        return recommendations.recommendations().stream()
                .map(recommendation -> getMovieByMovieId(recommendation.movieId()))
                .toList();
    }



}
