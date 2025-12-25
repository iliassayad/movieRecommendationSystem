package net.ayad.ingestionservice.config.batchListeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepProgressListener implements StepExecutionListener {

    private long startTime;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        startTime = System.currentTimeMillis();
        log.info("🚀 Démarrage du step: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long duration = (System.currentTimeMillis() - startTime) / 1000;

        log.info("✅ Step '{}' terminé en {}s", stepExecution.getStepName(), duration);
        log.info("   📊 Statistiques:");
        log.info("      - Lu: {}", stepExecution.getReadCount());
        log.info("      - Écrit: {}", stepExecution.getWriteCount());
        log.info("      - Erreurs: {}", stepExecution.getSkipCount());
        log.info("      - Commits: {}", stepExecution.getCommitCount());

        return stepExecution.getExitStatus();
    }


}
