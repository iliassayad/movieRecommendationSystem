package net.ayad.ingestionservice.config.batchListeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChunkProgressListener implements ChunkListener {
    private long startTime;
    private int chunkCount = 0;

    @Override
    public void beforeChunk(ChunkContext context) {
        startTime = System.currentTimeMillis();
        chunkCount++;
    }


    @Override
    public void afterChunk(ChunkContext context) {
        long duration = System.currentTimeMillis() - startTime;
        long readCount = context.getStepContext().getStepExecution().getReadCount();
        long writeCount = context.getStepContext().getStepExecution().getWriteCount();

        log.info("✅ Chunk #{} terminé en {}ms - Total lu: {} - Total écrit: {}",
                chunkCount, duration, readCount, writeCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("❌ Erreur dans le chunk #{}", chunkCount);
    }
}
