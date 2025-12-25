package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.entity.Movie;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieBulkUpsertWriter implements ItemWriter<Movie> {

    private final MongoTemplate mongoTemplate;

    @Override
    public void write(Chunk<? extends Movie> chunk) throws Exception {
        BulkOperations bulkOps = mongoTemplate.bulkOps(
                BulkOperations.BulkMode.UNORDERED,
                Movie.class
        );

        for (Movie movie : chunk) {
            Query query = new Query(Criteria.where("movieId").is(movie.getMovieId()));

            Update update = new Update()
                    .set("movieId", movie.getMovieId())
                    .set("tmdbId", movie.getTmdbId())
                    .set("imdbId", movie.getImdbId())
                    .set("title", movie.getTitle())
                    .set("originalTitle", movie.getOriginalTitle())
                    .set("overview", movie.getOverview())
                    .set("tagline", movie.getTagline())
                    .set("releaseDate", movie.getReleaseDate())
                    .set("runtime", movie.getRuntime())
                    .set("status", movie.getStatus())
                    .set("budget", movie.getBudget())
                    .set("revenue", movie.getRevenue())
                    .set("voteAverage", movie.getVoteAverage())
                    .set("voteCount", movie.getVoteCount())
                    .set("popularity", movie.getPopularity())
                    .set("genres", movie.getGenres())
                    .set("originalLanguage", movie.getOriginalLanguage())
                    .set("spokenLanguages", movie.getSpokenLanguages())
                    .set("posterPath", movie.getPosterPath())
                    .set("backdropPath", movie.getBackdropPath())
                    .set("adult", movie.getAdult());

            bulkOps.upsert(query, update);
        }
        bulkOps.execute();
    }
}
