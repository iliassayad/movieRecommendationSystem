package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RatingRepository extends MongoRepository<Rating, String> {
}
