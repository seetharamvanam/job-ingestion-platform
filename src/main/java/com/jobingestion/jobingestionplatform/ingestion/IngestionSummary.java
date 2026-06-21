package com.jobingestion.jobingestionplatform.ingestion;

public record IngestionSummary(
        int jobsFound,
        int jobsInserted,
        int jobsSkipped
) {
}
