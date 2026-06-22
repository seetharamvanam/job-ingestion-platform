package com.jobingestion.jobingestionplatform.job;

public record JobSummaryResponse(
        Long id,
        String title,
        String companyName,
        String location,
        String department,
        String jobUrl
) {
}
