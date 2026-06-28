package com.jobingestion.jobingestionplatform.provider.model;

public record ScrapedJob(
        String externalJobId,
        String title,
        String department,
        String location,
        String jobUrl,
        String description
        ) {
}
