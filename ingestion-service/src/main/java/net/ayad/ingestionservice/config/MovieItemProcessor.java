package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ayad.ingestionservice.entity.Genre;
import net.ayad.ingestionservice.entity.Link;
import net.ayad.ingestionservice.entity.Movie;
import net.ayad.ingestionservice.repository.GenreRepository;
import net.ayad.ingestionservice.repository.MovieRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MovieItemProcessor implements ItemProcessor<Link, Movie> {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Override
    public Movie process(Link link) {

        if (link.getTmdbId() == null) {
            log.warn("TMDB ID is null for movieId={}", link.getMovieId());
            return null;
        }

        String url = "https://api.themoviedb.org/3/movie/"
                + link.getTmdbId() + "?api_key=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) return null;

        Movie movie = new Movie();

        movie.setMovieId(link.getMovieId());
        movie.setTmdbId(link.getTmdbId());
        movie.setImdbId(link.getImdbId());

        movie.setTitle((String) response.get("title"));
        movie.setOriginalTitle((String) response.get("original_title"));
        movie.setOverview((String) response.get("overview"));
        movie.setTagline((String) response.get("tagline"));

        movie.setReleaseDate((String) response.get("release_date"));
        movie.setRuntime(response.get("runtime") != null
                ? ((Number) response.get("runtime")).intValue()
                : null);

        movie.setStatus((String) response.get("status"));
        movie.setBudget(toLong(response.get("budget")));
        movie.setRevenue(toLong(response.get("revenue")));

        movie.setVoteAverage(response.get("vote_average") != null
                ? ((Number) response.get("vote_average")).doubleValue()
                : null);

        movie.setVoteCount(response.get("vote_count") != null
                ? ((Number) response.get("vote_count")).intValue()
                : null);

        movie.setPopularity(response.get("popularity") != null
                ? ((Number) response.get("popularity")).doubleValue()
                : null);

        movie.setPosterPath((String) response.get("poster_path"));
        movie.setBackdropPath((String) response.get("backdrop_path"));

        movie.setOriginalLanguage((String) response.get("original_language"));
        movie.setSpokenLanguages((List<String>) response.get("spoken_languages"));
        movie.setAdult((Boolean) response.get("adult"));

        // 🔥 GENRES (séparés)
        if (response.get("genres") != null) {
            List<Map<String, Object>> genres =
                    (List<Map<String, Object>>) response.get("genres");

            List<Long> genreIds = new ArrayList<>();

            for (Map<String, Object> g : genres) {
                Long genreId = ((Number) g.get("id")).longValue();
                String name = (String) g.get("name");

                genreRepository.save(new Genre(genreId, name));
                genreIds.add(genreId);
            }

            movie.setGenreIds(genreIds);
        }

        return movie;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number n) return n.longValue();
        return Long.parseLong(value.toString());
    }
}
