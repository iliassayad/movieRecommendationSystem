package net.ayad.moviebffservice.feign;

import net.ayad.moviebffservice.model.MovieDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "INGESTION-SERVICE", path = "/ingestion")
public interface MovieRestClient {

    @GetMapping
    List<MovieDTO> getAllMovies();

    @GetMapping("/{movieId}")
    MovieDTO getMovieByMovieId(@PathVariable Long movieId);

}
