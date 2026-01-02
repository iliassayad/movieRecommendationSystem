package net.ayad.ingestionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailDTO {
    private Long movieId;
    private Long tmdbId;
    private String imdbId;

    private String title;
    private String originalTitle;
    private String overview;
    private String tagline;

    // Dates et durée
    private String releaseDate;
    private Integer runtime;
    private String status;

    // Financier
    private Long budget;
    private Long revenue;

    // Évaluations
    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;

    // Genres et langues
    private List<GenreDTO> genres;
    private String originalLanguage;
    private List<String> spokenLanguages;

    // Visuels
    private String posterPath;
    private String backdropPath;

    // Métadonnées
    private Boolean adult;
}
