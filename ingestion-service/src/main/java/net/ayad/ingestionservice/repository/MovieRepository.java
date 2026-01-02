package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findByMovieId(Long movieId);

    Page<Movie> findByGenreIdsContaining(Long genreId, PageRequest pageRequest);

    List<Movie> findTopNByOrderByPopularityDesc(int limit);

    Page<Movie> findByTitleContainingIgnoreCase(String query, PageRequest pageRequest);
}
