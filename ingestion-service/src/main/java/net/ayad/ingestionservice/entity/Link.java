package net.ayad.ingestionservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "links")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Link {
    @Id
    private String id;
    private Long movieId;
    private String imdbId;
    private Long tmdbId;
}
