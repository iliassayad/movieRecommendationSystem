package net.ayad.moviebffservice.feign;

import net.ayad.moviebffservice.model.RecommendationDTO;
import net.ayad.moviebffservice.model.UserRecommendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "RECOMMENDATION-SERVICE")
public interface RecommendationRestClient {

    @PostMapping("/recommendations")
    RecommendationDTO recommend(@RequestBody UserRecommendRequest request);
}
