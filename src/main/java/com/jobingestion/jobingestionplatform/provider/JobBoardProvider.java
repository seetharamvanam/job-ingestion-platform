package com.jobingestion.jobingestionplatform.provider;

import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.source.JobBoardProviderType;
import com.jobingestion.jobingestionplatform.source.JobSource;

import java.util.List;

public interface JobBoardProvider {

    List<ScrapedJob> fetchJobs(JobSource jobSource);
    ScrapedJob fetchJobDetails(ScrapedJob job);
    JobBoardProviderType getProviderType();
}
