package net.ayad.ingestionservice.service;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.dto.GenreDTO;
import net.ayad.ingestionservice.entity.Genre;
import net.ayad.ingestionservice.mapper.GenreMapper;
import net.ayad.ingestionservice.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public List<GenreDTO> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genreMapper::toGenreDTO)
                .toList();
    }


    public GenreDTO getGenreById(Long genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found with id: " + genreId));
        return genreMapper.toGenreDTO(genre);
    }
}
