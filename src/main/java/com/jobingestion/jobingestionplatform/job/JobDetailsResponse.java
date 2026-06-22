package com.jobingestion.jobingestionplatform.job;

import java.time.LocalDateTime;

public record JobDetailsResponse(
        Long id,
        String title,
        String companyName,
        String location,
        String department,
        String jobUrl,
        String jobDescription,
        LocalDateTime discoveredAt
) {
}
