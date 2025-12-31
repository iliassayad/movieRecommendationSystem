package net.ayad.moviebffservice.model;

import java.util.List;

public record MovieDTO(
        Long movieId,
        String title,
        String overview,
        String releaseDate,
        Integer runtime,
        List<String> genres,
        String originalLanguage,
        String posterPath,
        Boolean adult
) {
}
