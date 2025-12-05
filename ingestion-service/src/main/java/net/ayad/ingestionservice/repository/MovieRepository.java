package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
}
