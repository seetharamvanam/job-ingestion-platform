package com.jobingestion.jobingestionplatform.filter;

import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;

public interface JobFilter {
    boolean isRelevant(ScrapedJob scrapedJob);
}
