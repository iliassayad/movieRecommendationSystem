package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.entity.Rating;
import net.ayad.ingestionservice.repository.RatingRepository;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class RatingItemProcessor implements ItemProcessor<Rating, Rating> {

    private final RatingRepository ratingRepository;

    @Override
    public Rating process(Rating item) throws Exception {

        Rating existing = ratingRepository.findByMovieIdAndUserId(item.getMovieId(), item.getUserId()).orElse(null);

        if (existing != null) {
            item.setId(existing.getId()); // pour mettre à jour au lieu d’insérer
        }

        return item;
    }
}
