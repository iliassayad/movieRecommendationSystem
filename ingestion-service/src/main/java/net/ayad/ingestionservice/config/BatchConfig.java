package net.ayad.ingestionservice.config;


import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.entity.Link;
import net.ayad.ingestionservice.entity.Movie;
import net.ayad.ingestionservice.entity.Rating;
import net.ayad.ingestionservice.repository.LinkRepository;
import net.ayad.ingestionservice.repository.MovieRepository;
import net.ayad.ingestionservice.repository.RatingRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;


@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final MovieRepository movieRepository;
    private final LinkRepository linkRepository;
    private final RatingRepository ratingRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public FlatFileItemReader<Link> linkItemReader() {
        return new FlatFileItemReaderBuilder<Link>()
                .name("linkItemReader")
                .resource(new FileSystemResource("src/main/resources/links.csv"))
                .linesToSkip(1)
                .delimited()
                .names("movieId", "imdbId", "tmdbId")
                .targetType(Link.class)
                .build();
    }


    @Bean
    public LinkItemProcessor linkItemProcessor() {
        return new LinkItemProcessor(linkRepository);
    }


    @Bean
    public RepositoryItemWriter<Link> repositoryItemWriter() {
        RepositoryItemWriter<Link> writer = new RepositoryItemWriter<>();
        writer.setRepository(linkRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step linkStep(){
        return new StepBuilder("linkStep", jobRepository)
                .<Link, Link>chunk(100, transactionManager)
                .reader(linkItemReader())
                .processor(linkItemProcessor())
                .writer(repositoryItemWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Rating> ratingItemReader() {
        return new FlatFileItemReaderBuilder<Rating>()
                .name("ratingItemReader")
                .resource(new FileSystemResource("src/main/resources/ratings.csv"))
                .linesToSkip(1)
                .delimited()
                .names("userId", "movieId", "rating", "timestamp")
                .targetType(Rating.class)
                .build();
    }

    @Bean
    public RatingItemProcessor ratingItemProcessor() {
        return new RatingItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Rating> ratingItemWriter() {
        RepositoryItemWriter<Rating> writer = new RepositoryItemWriter<>();
        writer.setRepository(ratingRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step ratingStep(){
        return new StepBuilder("ratingStep", jobRepository)
                .<Rating, Rating>chunk(100, transactionManager)
                .reader(ratingItemReader())
                .processor(ratingItemProcessor())
                .writer(ratingItemWriter())
                .build();
    }


    //Movie with TMDB API

    @Bean
    public RepositoryItemReader<Link> movieItemReader() {
        RepositoryItemReader<Link> reader = new RepositoryItemReader<>();
        reader.setRepository(linkRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(100);
        reader.setSort(Collections.singletonMap("movieId", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public MovieItemProcessor movieItemProcessor() {
        return new MovieItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Movie> movieItemWriter() {
        RepositoryItemWriter<Movie> writer = new RepositoryItemWriter<>();
        writer.setRepository(movieRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step movieStep(){
        return new StepBuilder("movieStep", jobRepository)
                .<Link, Movie>chunk(50, transactionManager)
                .reader(movieItemReader())
                .processor(movieItemProcessor())
                .writer(movieItemWriter())
                .faultTolerant()
                .skip(HttpClientErrorException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }








    @Bean
    public Job linkJob() {
        return new JobBuilder("linkJob", jobRepository)
                .start(linkStep())
                .next(ratingStep())
                .next(movieStep())
                .build();
    }
}
