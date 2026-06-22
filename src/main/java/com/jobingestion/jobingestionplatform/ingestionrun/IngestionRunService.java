package com.jobingestion.jobingestionplatform.ingestionrun;

import com.jobingestion.jobingestionplatform.ingestion.IngestionSummary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngestionRunService {

    private final IngestionRunRepository ingestionRunRepository;

    public IngestionRunService(IngestionRunRepository ingestionRunRepository) {
        this.ingestionRunRepository = ingestionRunRepository;
    }


    public IngestionRun startRun() {
        IngestionRun ingestionRun = IngestionRun.builder()
                .startedAt(LocalDateTime.now())
                .status(IngestionRunStatus.RUNNING)
                .build();
        return ingestionRunRepository.save(ingestionRun);
    }

    public void markSuccess(IngestionRun run, IngestionSummary summary) {
        run.setCompletedAt(LocalDateTime.now());
        run.setStatus(IngestionRunStatus.SUCCESS);
        run.setJobsFound(summary.jobsFound());
        run.setJobsInserted(summary.jobsInserted());
        run.setJobsSkipped(summary.jobsSkipped());
        ingestionRunRepository.save(run);
    }

    public void markFailed(IngestionRun run, String errorMessage) {
        run.setCompletedAt(LocalDateTime.now());
        run.setStatus(IngestionRunStatus.FAILED);
        run.setErrorMessage(errorMessage);
        ingestionRunRepository.save(run);
    }

    public List<IngestionRunDetails> searchAllIngestionRuns(){
        List<IngestionRun> ingestionRuns = ingestionRunRepository.findAll();
        List<IngestionRunDetails> ingestionRunDetailsList = new ArrayList<>();
        for (IngestionRun ingestionRun : ingestionRuns) {
            ingestionRunDetailsList.add(
                    new IngestionRunDetails(
                            ingestionRun.getId(),
                            ingestionRun.getStartedAt(),
                            ingestionRun.getCompletedAt(),
                            ingestionRun.getStatus(),
                            ingestionRun.getJobsFound(),
                            ingestionRun.getJobsInserted(),
                            ingestionRun.getJobsSkipped(),
                            ingestionRun.getErrorMessage()
                    )
            );
        }
        return ingestionRunDetailsList;
    }

    public IngestionRunDetails searchIngestionRun(Long id){
        IngestionRun  ingestionRun = ingestionRunRepository.findById(id).orElse( null);
        return new IngestionRunDetails(
                ingestionRun.getId(),
                ingestionRun.getStartedAt(),
                ingestionRun.getCompletedAt(),
                ingestionRun.getStatus(),
                ingestionRun.getJobsFound(),
                ingestionRun.getJobsInserted(),
                ingestionRun.getJobsSkipped(),
                ingestionRun.getErrorMessage()
        );
    }
}
