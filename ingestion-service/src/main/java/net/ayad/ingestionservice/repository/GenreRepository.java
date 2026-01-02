package net.ayad.ingestionservice.repository;

import net.ayad.ingestionservice.entity.Genre;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GenreRepository extends MongoRepository<Genre, Long> {


}
