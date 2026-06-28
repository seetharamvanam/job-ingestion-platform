package com.jobingestion.jobingestionplatform.job.mapper;

import com.jobingestion.jobingestionplatform.job.JobPosting;
import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.source.JobSource;
import org.springframework.stereotype.Component;

@Component
public class JobPostingMapper {

    public JobPosting toEntity(ScrapedJob jobDetails, JobSource jobSource){
        return new JobPosting(
                jobDetails.externalJobId(),
                jobDetails.title(),
                jobDetails.department(),
                jobDetails.location(),
                jobDetails.jobUrl(),
                jobSource,
                jobDetails.description());
    }
}
