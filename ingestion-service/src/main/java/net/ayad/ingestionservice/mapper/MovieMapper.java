package net.ayad.ingestionservice.mapper;

import net.ayad.ingestionservice.dto.MovieDTO;
import net.ayad.ingestionservice.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "genres", expression = "java(new java.util.ArrayList<>(movie.getGenres().values()))")
    MovieDTO toMovieDTO(Movie movie);
}
