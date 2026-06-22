package com.jobingestion.jobingestionplatform.scheduler;

import com.jobingestion.jobingestionplatform.ingestion.JobIngestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class JobIngestionScheduler {

    private final JobIngestionService jobIngestionService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    public JobIngestionScheduler(JobIngestionService jobIngestionService){
        this.jobIngestionService = jobIngestionService;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.ingestion-fixed-delay-ms}")
    public void run(){
        if(isRunning.compareAndSet(false,true)){
            try {
                log.info("Job ingestion triggered by scheduler");
                log.info("Scheduler completed: {}", jobIngestionService.ingestJobs());
            }
            finally {
                isRunning.set(false);
            }
        }else{
            log.warn("Job ingestion already running");
        }


    }
}
