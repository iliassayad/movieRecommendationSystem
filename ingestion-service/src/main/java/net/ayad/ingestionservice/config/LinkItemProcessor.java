package net.ayad.ingestionservice.config;

import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.entity.Link;
import net.ayad.ingestionservice.repository.LinkRepository;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor
public class LinkItemProcessor implements ItemProcessor<Link, Link> {

    private final LinkRepository linkRepository;
    @Override
    public Link process(Link item) throws Exception {

        Link existing = linkRepository.findByMovieId(item.getMovieId()).orElse(null);

        if (existing != null) {
            item.setId(existing.getId()); // pour mettre à jour au lieu d’insérer
        }
        return item;
    }
}
