package net.ayad.ingestionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Batch Ingestion Controller", description = "APIs for batch data ingestion")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ingestion")
public class BatchIngestionController {


    public final JobLauncher jobLauncher;
    private final Job job;


    @Operation(summary = "Import Data", description = "Trigger the batch job to import data into the system")
    @ApiResponse(responseCode = "200", description = "Batch job to import data triggered successfully")
    @PostMapping("/import-data")
    public void importData() {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(job, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
