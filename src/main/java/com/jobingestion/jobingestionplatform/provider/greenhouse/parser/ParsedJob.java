package com.jobingestion.jobingestionplatform.provider.greenhouse.parser;

public record ParsedJob(
        Long externalJobId,
        String title,
        String department,
        String location,
        String jobUrl,
        String jobDescription
) {
}
