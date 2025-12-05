package net.ayad.ingestionservice.entity;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Document(collection = "movies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {

    private String id;

    @Indexed(unique = true)
    private Long movieId; // MovieLens ID

    private Long tmdbId;
    private String imdbId;


    // Données de base
    private String title;
    private String originalTitle;
    private String overview;
    private String tagline;

    // Métadonnées
    private String releaseDate;
    private Integer runtime;
    private String status;
    private Long budget;
    private Long revenue;

    // Évaluations
    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;

    // Genres et langues
    private Map<Long, String> genres; // Map of genre ID to genre name
    private String originalLanguage;
    private List<String> spokenLanguages;

    // Visuels
    private String posterPath;
    private String backdropPath;

    // Métadonnées d'enrichissement
    private Boolean adult;



}
