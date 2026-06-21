package com.jobingestion.jobingestionplatform.filter;

import com.jobingestion.jobingestionplatform.parser.ParsedJob;

public interface JobFilter {
    boolean isRelevant(ParsedJob parsedJob);
}
