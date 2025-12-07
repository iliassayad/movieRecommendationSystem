package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findByMovieId(Long movieId);
}
