package com.jobingestion.jobingestionplatform.scheduler;

import com.jobingestion.jobingestionplatform.ingestion.JobIngestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobIngestionScheduler {

    private final JobIngestionService jobIngestionService;
    public JobIngestionScheduler(JobIngestionService jobIngestionService){
        this.jobIngestionService = jobIngestionService;
    }

    @Scheduled(fixedDelay = 120000)
    public void run(){
        log.info("Job ingestion triggered by scheduler");
        log.info("Scheduler completed: {}", jobIngestionService.ingestJobs());
    }
}
