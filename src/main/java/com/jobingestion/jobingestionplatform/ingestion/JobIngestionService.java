package com.jobingestion.jobingestionplatform.ingestion;

import com.jobingestion.jobingestionplatform.job.mapper.JobPostingMapper;
import com.jobingestion.jobingestionplatform.provider.JobBoardProvider;
import com.jobingestion.jobingestionplatform.provider.JobBoardProviderFactory;
import com.jobingestion.jobingestionplatform.filter.JobFilter;
import com.jobingestion.jobingestionplatform.job.JobPosting;
import com.jobingestion.jobingestionplatform.job.JobPostingRepository;
import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.source.JobSource;
import com.jobingestion.jobingestionplatform.source.JobSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JobIngestionService {

    private final JobFilter jobFilter;
    private final JobPostingRepository jobPostingRepository;
    private final JobSourceRepository jobSourceRepository;
    private final JobBoardProviderFactory providerFactory;
    private final JobPostingMapper jobPostingMapper;

    public JobIngestionService(
            JobFilter jobFilter,
            JobPostingRepository jobPostingRepository,
            JobSourceRepository jobSourceRepository,
            JobBoardProviderFactory providerFactory,
            JobPostingMapper jobPostingMapper
    ) {

        this.jobFilter = jobFilter;
        this.jobPostingRepository = jobPostingRepository;
        this.jobSourceRepository = jobSourceRepository;
        this.providerFactory = providerFactory;
        this.jobPostingMapper = jobPostingMapper;
    }

    public IngestionSummary ingestJobs() {
        long startTime = System.currentTimeMillis();
        // Initialize the parameters found, skipped and Inserted to zero
        int found = 0;
        int skipped = 0;
        int inserted = 0;
        List<JobSource> activeSourceList = jobSourceRepository.findByActiveStatusTrue();
        if (activeSourceList.isEmpty()) {
            log.warn("No active job sources available for ingestion");
        }
        for (JobSource jobSource : activeSourceList) {
            IngestionSummary summary = processSource(jobSource);
            found += summary.jobsFound();
            skipped += summary.jobsSkipped();
            inserted += summary.jobsInserted();
        }
        long endTime = System.currentTimeMillis();
        log.info(
                "Ingestion completed in {} ms. Found {}, Inserted {}, Skipped {}",
                endTime - startTime,
                found,
                inserted,
                skipped
        );
        return new IngestionSummary(found, inserted, skipped);
    }


    private IngestionSummary processSource(JobSource jobSource) {
        int found = 0;
        int skipped = 0;
        int inserted = 0;
        List<JobPosting> listOfJobPostings = new ArrayList<>();
        JobBoardProvider provider = providerFactory.getProvider(jobSource.getProvider());
        // Method call to fetch jobs in greenHouseProvider to get list of jobs from a single jobSource
        List<ScrapedJob> listOfJobs = provider.fetchJobs(jobSource);
        found += listOfJobs.size();
        for (ScrapedJob scrapedJob : listOfJobs) {
            // For each Job from the list of Jobs scraped, we are checking whether the job is relevant to
            // Software Engineering.
            boolean isRelevantJob = jobFilter.isRelevant(scrapedJob);
            // Checking whether the job already exists in the database.
            boolean isDuplicateJob = jobPostingRepository.existsByJobSourceAndExternalJobId(
                    jobSource, scrapedJob.externalJobId());
            if (isDuplicateJob) {
                skipped++;
                continue;
            }
            if (!isRelevantJob) {
                skipped++;
                continue;
            }
            // If the job posting is relevant job posting and it does not exist in database, then we are
            // retrieving job description by scraping the job posting page and saving the data into the database.
            ScrapedJob enrichedJob = provider.fetchJobDetails(scrapedJob);
            listOfJobPostings.add(jobPostingMapper.toEntity(enrichedJob, jobSource));
            inserted++;
        }
        jobPostingRepository.saveAll(listOfJobPostings);

        return new IngestionSummary(found, inserted, skipped);
    }
}
