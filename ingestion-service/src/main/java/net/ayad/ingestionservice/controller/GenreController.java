package net.ayad.ingestionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.dto.GenreDTO;
import net.ayad.ingestionservice.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Genre Controller", description = "APIs for managing genres")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
public class GenreController {

    private final GenreService genreService;

    @Operation(summary = "Get All Genres", description = "Retrieve a list of all genres")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of genres")
    @GetMapping
    public ResponseEntity<List<GenreDTO>> genres() {
        List<GenreDTO> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @Operation(summary = "Get Genre by ID", description = "Retrieve a genre by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the genre")
    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> genreById(@PathVariable Long id) {
        GenreDTO genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }



}
