package com.jobingestion.jobingestionplatform.job;

import java.util.List;

public record JobPageResponse(
        List<JobSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
