package net.ayad.ingestionservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ratings")
@CompoundIndex(name = "user_movie_idx", def = "{'userId': 1, 'movieId': 1}", unique = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    @Id
    private String id;

    private Long userId;
    private Long movieId;
    private Double rating;
    private String timestamp;
}
