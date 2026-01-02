package net.ayad.moviebffservice.model;

import java.util.List;

public record MovieDTO(
        Long movieId,
        String title,
        String overview,

        String releaseDate,

        Double voteAverage,
        Integer voteCount,
        Double popularity,

        String posterPath,
        String backdropPath,

        List<Long> genreIds,
        List<String> genreNames,

        String originalLanguage
) {
}
