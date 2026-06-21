package com.jobingestion.jobingestionplatform.filter;

import com.jobingestion.jobingestionplatform.parser.ParsedJob;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SoftwareJobFilter implements JobFilter{

    private static final List<String> KEYWORDS = List.of(
            "engineering",
            "software",
            "backend",
            "frontend",
            "full stack",
            "fullstack",
            "java",
            "platform",
            "infrastructure",
            "distributed systems",
            "api",
            "microservices",
            "cloud"
    );

    @Override
    public boolean isRelevant(ParsedJob parsedJob) {
        String text = (parsedJob.title() + " " + parsedJob.department()).toLowerCase();
        return KEYWORDS.stream().anyMatch(text::contains);
    }
}
