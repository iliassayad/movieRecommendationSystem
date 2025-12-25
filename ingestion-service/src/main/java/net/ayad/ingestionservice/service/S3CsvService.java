package net.ayad.ingestionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ayad.ingestionservice.config.TempFileCleanupListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3CsvService {

    private final TempFileCleanupListener cleanupListener;

    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

//    public Resource getLatestFile(String prefix) {
//        List<S3Object> objects = s3Client.listObjectsV2(ListObjectsV2Request.builder()
//                .bucket(bucketName)
//                .prefix(prefix)
//                .build())
//                .contents();
//
//        if (objects.isEmpty()) return null;
//
//        // pick the latest by lastModified
//        S3Object latest = objects.stream()
//                .max((o1, o2) -> o1.lastModified().compareTo(o2.lastModified()))
//                .get();
//
//        InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(latest.key())
//                .build());
//
//        return new InputStreamResource(inputStream);
//    }

    public FileSystemResource downloadLatestFileToLocal(String prefix){
        String latestKey = findLatestS3Key(prefix);

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("batch-" + prefix + "-", ".csv");
            Files.delete(tempFile);
        } catch (IOException e) {
           log.error(e.getMessage());
            throw new RuntimeException("Failed to create temp file", e);
        }


        s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(latestKey)
                        .build(),
                tempFile
        );

        cleanupListener.registerTempFile(tempFile);

        return new FileSystemResource(tempFile.toFile());
    }

    private String findLatestS3Key(String prefix) {
        List<S3Object> objects = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build())
                .contents();

        if (objects.isEmpty()) {
            throw new RuntimeException("No objects found with prefix: " + prefix);
        }

        S3Object latest = objects.stream()
                .max((o1, o2) -> o1.lastModified().compareTo(o2.lastModified()))
                .get();

        return latest.key();
    }
}
