package net.ayad.ingestionservice.mapper;

import net.ayad.ingestionservice.dto.MovieDTO;
import net.ayad.ingestionservice.dto.MovieDetailDTO;
import net.ayad.ingestionservice.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieDTO toMovieDTO(Movie movie);

    MovieDetailDTO toMovieDetailDTO(Movie movie);
}
