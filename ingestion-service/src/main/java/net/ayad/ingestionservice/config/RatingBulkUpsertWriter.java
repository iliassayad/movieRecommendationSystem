package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.entity.Rating;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingBulkUpsertWriter implements ItemWriter<Rating> {

    private final MongoTemplate mongoTemplate;

    @Override
    public void write(Chunk<? extends Rating> chunk) throws Exception {

        BulkOperations bulkOps =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Rating.class);

        for(Rating rating: chunk){
            Query query = new Query(
                    Criteria.where("userId").is(rating.getUserId())
                            .and("movieId").is(rating.getMovieId())
            );


            Update update = new Update()
                    .set("rating", rating.getRating())
                    .set("timestamp", rating.getTimestamp())
                    .set("userId", rating.getUserId())
                    .set("movieId", rating.getMovieId());

            bulkOps.upsert(query, update);
        }

        bulkOps.execute();

    }
}
