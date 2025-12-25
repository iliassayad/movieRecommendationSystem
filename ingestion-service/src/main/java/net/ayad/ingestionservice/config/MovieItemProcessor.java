package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ayad.ingestionservice.entity.Link;
import net.ayad.ingestionservice.entity.Movie;
import net.ayad.ingestionservice.repository.MovieRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MovieItemProcessor implements ItemProcessor<Link, Movie> {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    @Value("${tmdb.api.key}")
    private String apiKey;


    @Override
    public Movie process(Link link) throws Exception {
        if (link.getTmdbId() == null) {
            log.error("TMDB ID is null");
            return null; // IMPORTANT : skip
        }
        String url = "https://api.themoviedb.org/3/movie/" + link.getTmdbId() + "?api_key=" + apiKey;

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
        movie.setRuntime(response.get("runtime") != null ? ((Number) response.get("runtime")).intValue() : null);
        movie.setStatus((String) response.get("status"));
        movie.setBudget(toLong(response.get("budget")));
        movie.setRevenue(toLong(response.get("revenue")));

        //Evaluation
        movie.setVoteAverage(response.get("vote_average") != null ? ((Number) response.get("vote_average")).doubleValue() : null);
        movie.setVoteCount(response.get("vote_count") != null ? ((Number) response.get("vote_count")).intValue() : null);
        movie.setPopularity((Double) response.get("popularity"));

        //Visuals
        movie.setPosterPath((String) response.get("poster_path"));
        movie.setBackdropPath((String) response.get("backdrop_path"));

        //genres
        if (response.get("genres") != null) {
            List<Map<String, Object>> genresList = (List<Map<String, Object>>) response.get("genres");
            Map<Long, String> genresMap = genresList.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            genre -> ((Number) genre.get("id")).longValue(),
                            genre -> (String) genre.get("name")
                    ));
            movie.setGenres(genresMap);
        }

        //original language
        movie.setOriginalLanguage((String) response.get("original_language"));
        movie.setSpokenLanguages((List<String>) response.get("spoken_languages"));


        //adult
        movie.setAdult(response.get("adult") != null ? (Boolean) response.get("adult") : null);

        return movie;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Integer i) return i.longValue();
        if (value instanceof Long l) return l;
        if (value instanceof Double d) return d.longValue();
        return Long.parseLong(value.toString());
    }
}
