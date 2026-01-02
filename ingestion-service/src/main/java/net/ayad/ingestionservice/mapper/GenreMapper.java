package net.ayad.ingestionservice.mapper;


import net.ayad.ingestionservice.dto.GenreDTO;
import net.ayad.ingestionservice.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") 
public interface GenreMapper {
    GenreDTO toGenreDTO(Genre genre);
}
