package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Link;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LinkRepository extends MongoRepository<Link, String> {
    Optional<Link> findByMovieId(Long movieId);
}
