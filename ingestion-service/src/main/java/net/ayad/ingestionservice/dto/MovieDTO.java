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
public class MovieDTO {
    private Long movieId;
    private String title;
    private String overview;

    private String releaseDate;

    private Double voteAverage;
    private Integer voteCount;
    private Double popularity;

    private String posterPath;
    private String backdropPath;

    private List<Long> genreIds;
    private List<String> genreNames;

    private String originalLanguage;

}
