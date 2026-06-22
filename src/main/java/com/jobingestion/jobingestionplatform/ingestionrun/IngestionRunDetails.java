package com.jobingestion.jobingestionplatform.ingestionrun;

import java.time.LocalDateTime;

public record IngestionRunDetails(
        Long id,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        IngestionRunStatus status,
        Integer jobsFound,
        Integer jobsInserted,
        Integer jobsSkipped,
        String errorMessage
) {
}
