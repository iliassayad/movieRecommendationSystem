package net.ayad.ingestionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3CsvService {
    private final S3Client s3Client;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public Resource getLatestFile(String prefix) {
        List<S3Object> objects = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build())
                .contents();

        if (objects.isEmpty()) return null;

        // pick the latest by lastModified
        S3Object latest = objects.stream()
                .max((o1, o2) -> o1.lastModified().compareTo(o2.lastModified()))
                .get();

        InputStream inputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(latest.key())
                .build());

        return new InputStreamResource(inputStream);
    }
}
