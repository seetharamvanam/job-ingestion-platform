package com.jobingestion.jobingestionplatform.parser;

public record ParsedJob(
        Long externalJobId,
        String title,
        String department,
        String location,
        String jobUrl,
        String jobDescription
) {
}
