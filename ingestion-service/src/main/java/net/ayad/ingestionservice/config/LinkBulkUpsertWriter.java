package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.entity.Link;
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
public class LinkBulkUpsertWriter implements ItemWriter<Link> {

    private final MongoTemplate mongoTemplate;

    @Override
    public void write(Chunk<? extends Link> chunk) throws Exception {

        BulkOperations bulkOps =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Link.class);

        for(Link link: chunk){
            Query query = new Query(
                    Criteria.where("movieId").is(link.getMovieId())
            );

            Update update = new Update()
                    .set("imdbId", link.getImdbId())
                    .set("tmdbId", link.getTmdbId())
                    .set("movieId", link.getMovieId());
            bulkOps.upsert(query, update);
        }
        bulkOps.execute();
    }
}
