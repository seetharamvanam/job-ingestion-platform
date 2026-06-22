package com.jobingestion.jobingestionplatform.ingestion;

import com.jobingestion.jobingestionplatform.detail.JobDetail;
import com.jobingestion.jobingestionplatform.detail.JobDetailParser;
import com.jobingestion.jobingestionplatform.filter.JobFilter;
import com.jobingestion.jobingestionplatform.job.JobPosting;
import com.jobingestion.jobingestionplatform.job.JobPostingRepository;
import com.jobingestion.jobingestionplatform.parser.GreenhouseParser;
import com.jobingestion.jobingestionplatform.parser.ParsedJob;
import com.jobingestion.jobingestionplatform.scraper.GreenhouseScraper;
import com.jobingestion.jobingestionplatform.source.JobSource;
import com.jobingestion.jobingestionplatform.source.JobSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JobIngestionService {

    private final JobSourceRepository jobSourceRepository;
    private final GreenhouseScraper greenhouseScraper;
    private final GreenhouseParser greenhouseParser;
    private final JobPostingRepository jobPostingRepository;
    private final JobFilter jobFilter;
    private final JobDetailParser jobDetailParser;

    public JobIngestionService(
            JobSourceRepository jobSourceRepository,
            GreenhouseParser greenhouseParser,
            GreenhouseScraper greenhouseScraper,
            JobPostingRepository jobPostingRepository,
            JobFilter jobFilter,
            JobDetailParser jobDetailParser
    ){
        this.jobSourceRepository = jobSourceRepository;
        this.greenhouseParser = greenhouseParser;
        this.greenhouseScraper = greenhouseScraper;
        this.jobPostingRepository = jobPostingRepository;
        this.jobFilter = jobFilter;
        this.jobDetailParser = jobDetailParser;
    }


    public IngestionSummary ingestJobs() {
        long startTime = System.currentTimeMillis();
        int found = 0;
        int inserted = 0;
        int skipped = 0;
        List<JobSource> activeSourceList = jobSourceRepository.findByActiveStatusTrue();
        for (JobSource jobSource : activeSourceList) {
            try {
                Document firstPage = greenhouseScraper.scrapeJobBoard(jobSource.getCareerUrl());
                int totalPages = greenhouseParser.extractTotalPages(firstPage);
                IngestionSummary firstPageSummary = processDocument(firstPage, jobSource);
                found += firstPageSummary.jobsFound();
                inserted += firstPageSummary.jobsInserted();
                skipped += firstPageSummary.jobsSkipped();
                for (int page = 2; page <= totalPages; page++) {
                    String pageUrl = jobSource.getCareerUrl() + "?page=" + page;
                    Document pageDocument = greenhouseScraper.scrapeJobBoard(pageUrl);
                    IngestionSummary pageSummary = processDocument(pageDocument, jobSource);
                    found += pageSummary.jobsFound();
                    inserted += pageSummary.jobsInserted();
                    skipped += pageSummary.jobsSkipped();
                }
                log.info("Total pages found for {}: {}", jobSource.getCompanyName(),totalPages);

            } catch (Exception e) {
                log.error("Failed to ingest source: {}", jobSource.getCareerUrl(), e);
                continue;
            }
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

    private IngestionSummary processDocument(Document currentPage, JobSource jobSource){
        int found = 0;
        int inserted = 0;
        int skipped = 0;
            List<ParsedJob> parsedJobs = greenhouseParser.parse(currentPage);
            List<JobPosting> jobPostingList = new ArrayList<>();
            found += parsedJobs.size();
            for (ParsedJob parsedJob : parsedJobs) {
                if(!jobFilter.isRelevant(parsedJob)){
                    skipped++;
                    continue;
                }
                boolean exists = jobPostingRepository.existsByJobSourceAndExternalJobId(jobSource, parsedJob.externalJobId());
                if(exists){
                    skipped++;
                    continue;
                }
                String jobDescription = "";
                try {
                    Document jobPostingDetailsDocument = greenhouseScraper.scrapeJobBoard(parsedJob.jobUrl());
                    JobDetail jobPostingDetails = jobDetailParser.parse(jobPostingDetailsDocument);
                    jobDescription = jobPostingDetails.description();
                }catch (Exception e){
                    log.warn("Failed to parse job posting details: {}", parsedJob.jobUrl(), e);
                }
                JobPosting jobPosting = JobPosting.builder()
                        .externalJobId(parsedJob.externalJobId())
                        .title(parsedJob.title())
                        .department(parsedJob.department())
                        .location(parsedJob.location())
                        .jobUrl(parsedJob.jobUrl())
                        .jobSource(jobSource)
                        .jobDescription(jobDescription)
                        .build();
                jobPostingList.add(jobPosting);
                inserted++;
            }
            jobPostingRepository.saveAll(jobPostingList);
            return new IngestionSummary(found, inserted, skipped);
    }
}
