package net.ayad.ingestionservice.config;


import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import net.ayad.ingestionservice.config.batchListeners.ChunkProgressListener;
import net.ayad.ingestionservice.config.batchListeners.StepProgressListener;
import net.ayad.ingestionservice.entity.Link;
import net.ayad.ingestionservice.entity.Movie;
import net.ayad.ingestionservice.entity.Rating;
import net.ayad.ingestionservice.repository.GenreRepository;
import net.ayad.ingestionservice.repository.LinkRepository;
import net.ayad.ingestionservice.repository.MovieRepository;
import net.ayad.ingestionservice.repository.RatingRepository;
import net.ayad.ingestionservice.service.S3CsvService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;


@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final TempFileCleanupListener cleanupListener;

    private final S3CsvService s3CsvService;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final LinkRepository linkRepository;
    private final RatingRepository ratingRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final MongoTemplate mongoTemplate;

    //Writers
    private final LinkBulkUpsertWriter linkBulkUpsertWriter;
    private final RatingBulkUpsertWriter ratingBulkUpsertWriter;
    private final MovieBulkUpsertWriter movieBulkUpsertWriter;

    //Listeners
    private final StepProgressListener stepProgressListener;
    private final ChunkProgressListener chunkProgressListener;

    @Bean
    public FlatFileItemReader<Link> linkItemReader(S3CsvService s3CsvService) {
        return new FlatFileItemReaderBuilder<Link>()
                .name("linkItemReader")
                .resource(s3CsvService.downloadLatestFileToLocal("links"))
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


//    @Bean
//    public RepositoryItemWriter<Link> repositoryItemWriter() {
//        RepositoryItemWriter<Link> writer = new RepositoryItemWriter<>();
//        writer.setRepository(linkRepository);
//        writer.setMethodName("saveAll");
//        return writer;
//    }

    @Bean
    public MongoItemWriter<Link> mongoLinkItemWriter() {
        MongoItemWriter<Link> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate); // Inject MongoTemplate if needed
        writer.setCollection("links");
        return writer;
    }

    @Bean
    public Step linkStep(){
        return new StepBuilder("linkStep", jobRepository)
                .<Link, Link>chunk(2000, transactionManager)
                .reader(linkItemReader(s3CsvService))
                .writer(linkBulkUpsertWriter)
                .listener(chunkProgressListener)
                .listener(stepProgressListener)
                .build();
    }

    @Bean
    public FlatFileItemReader<Rating> ratingItemReader(S3CsvService s3CsvService) {
        return new FlatFileItemReaderBuilder<Rating>()
                .name("ratingItemReader")
                .resource(s3CsvService.downloadLatestFileToLocal("ratings"))
                .linesToSkip(1)
                .delimited()
                .names("userId", "movieId", "rating", "timestamp")
                .targetType(Rating.class)
                .build();
    }

    @Bean
    public RatingItemProcessor ratingItemProcessor() {
        return new RatingItemProcessor(ratingRepository);
    }

//    @Bean
//    public RepositoryItemWriter<Rating> ratingItemWriter() {
//        RepositoryItemWriter<Rating> writer = new RepositoryItemWriter<>();
//        writer.setRepository(ratingRepository);
//        writer.setMethodName("saveAll");
//        return writer;
//    }

    @Bean
    public MongoItemWriter<Rating> mongoRatingItemWriter() {
        MongoItemWriter<Rating> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate); // Inject MongoTemplate if needed
        writer.setCollection("ratings");
        return writer;
    }

    @Bean
    public Step ratingStep(){
        return new StepBuilder("ratingStep", jobRepository)
                .<Rating, Rating>chunk(50, transactionManager)
                .reader(ratingItemReader(s3CsvService))
                .writer(ratingBulkUpsertWriter)
                .listener(chunkProgressListener)
                .listener(stepProgressListener)
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
    public MovieItemProcessor movieItemProcessor(
            MovieRepository movieRepository,
            GenreRepository genreRepository
    ) {
        return new MovieItemProcessor(movieRepository, genreRepository);
    }


//    @Bean
//    public RepositoryItemWriter<Movie> movieItemWriter() {
//        RepositoryItemWriter<Movie> writer = new RepositoryItemWriter<>();
//        writer.setRepository(movieRepository);
//        writer.setMethodName("saveAll");
//        return writer;
//    }

    @Bean
    public MongoItemWriter<Movie> mongoMovieItemWriter() {
        MongoItemWriter<Movie> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate); // Inject MongoTemplate if needed
        writer.setCollection("movies");
        return writer;
    }

    @Bean
    public Step movieStep(){
        return new StepBuilder("movieStep", jobRepository)
                .<Link, Movie>chunk(100, transactionManager)
                .reader(movieItemReader())
                .processor(movieItemProcessor(movieRepository, genreRepository))
                .writer(movieBulkUpsertWriter)
                .listener(chunkProgressListener)
                .listener(stepProgressListener)
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
                .listener(cleanupListener)
                .build();
    }
}
