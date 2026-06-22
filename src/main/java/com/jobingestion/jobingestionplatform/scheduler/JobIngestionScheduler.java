package com.jobingestion.jobingestionplatform.scheduler;

import com.jobingestion.jobingestionplatform.ingestion.IngestionSummary;
import com.jobingestion.jobingestionplatform.ingestion.JobIngestionService;
import com.jobingestion.jobingestionplatform.ingestionrun.IngestionRun;
import com.jobingestion.jobingestionplatform.ingestionrun.IngestionRunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class JobIngestionScheduler {

    private final JobIngestionService jobIngestionService;
    private final IngestionRunService ingestionRunService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    public JobIngestionScheduler(JobIngestionService jobIngestionService,  IngestionRunService ingestionRunService) {
        this.jobIngestionService = jobIngestionService;
        this.ingestionRunService = ingestionRunService;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.ingestion-fixed-delay-ms}")
    public void run(){
        if(isRunning.compareAndSet(false,true)){
            IngestionRun run = null;
            try {

                log.info("Job ingestion triggered by scheduler");
                run = ingestionRunService.startRun();
                IngestionSummary ingestionSummary = jobIngestionService.ingestJobs();
                log.info("Scheduler completed: {}", ingestionSummary);
                ingestionRunService.markSuccess(run, ingestionSummary);
            } catch (Exception e) {
                log.error("Scheduled job ingestion failed", e);
                if(run != null){
                    ingestionRunService.markFailed(run, e.getMessage());
                }
            }
            finally {
                isRunning.set(false);
            }
        }else{
            log.warn("Job ingestion already running");
        }


    }
}
