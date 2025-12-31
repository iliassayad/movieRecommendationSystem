package net.ayad.ingestionservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TempFileCleanupListener implements JobExecutionListener {
    private final List<Path> tempFilesToCleanup = new ArrayList<>();

    public void registerTempFile(Path tempFile) {
        tempFilesToCleanup.add(tempFile);
        log.info("Registered temp file for cleanup: {}", tempFile);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Clear any previous temp files list
        tempFilesToCleanup.clear();
        log.info("Job started: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job completed with status: {}", jobExecution.getStatus());

        // Clean up all temp files
        for (Path tempFile : tempFilesToCleanup) {
            try {
                if (Files.exists(tempFile)) {
                    Files.delete(tempFile);
                    log.info("Successfully deleted temp file: {}", tempFile);
                }
            } catch (IOException e) {
                log.error("Failed to delete temp file: {}", tempFile, e);
            }
        }

        tempFilesToCleanup.clear();
        log.info("Temp file cleanup completed");
    }
}
