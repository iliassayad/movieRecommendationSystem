package net.ayad.ingestionservice.config;

import net.ayad.ingestionservice.entity.Rating;
import org.springframework.batch.item.ItemProcessor;

public class RatingItemProcessor implements ItemProcessor<Rating, Rating> {
    @Override
    public Rating process(Rating item) throws Exception {
        return item;
    }
}
