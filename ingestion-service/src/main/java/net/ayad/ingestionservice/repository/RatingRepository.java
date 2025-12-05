package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RatingRepository extends MongoRepository<Rating, String> {
    Optional<Rating> findByMovieIdAndUserId(Long movieId, Long userId);
}
