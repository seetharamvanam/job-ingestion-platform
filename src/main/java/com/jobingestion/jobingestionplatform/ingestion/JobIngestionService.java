package com.jobingestion.jobingestionplatform.ingestion;

import com.jobingestion.jobingestionplatform.job.JobPosting;
import com.jobingestion.jobingestionplatform.job.JobPostingRepository;
import com.jobingestion.jobingestionplatform.parser.GreenhouseParser;
import com.jobingestion.jobingestionplatform.parser.ParsedJob;
import com.jobingestion.jobingestionplatform.scraper.GreenhouseScraper;
import com.jobingestion.jobingestionplatform.source.JobSource;
import com.jobingestion.jobingestionplatform.source.JobSourceRepository;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobIngestionService {

    private final JobSourceRepository jobSourceRepository;
    private final GreenhouseScraper greenhouseScraper;
    private final GreenhouseParser greenhouseParser;
    private final JobPostingRepository jobPostingRepository;

    public JobIngestionService(
            JobSourceRepository jobSourceRepository,
            GreenhouseParser greenhouseParser,
            GreenhouseScraper greenhouseScraper,
            JobPostingRepository jobPostingRepository
    ){
        this.jobSourceRepository = jobSourceRepository;
        this.greenhouseParser = greenhouseParser;
        this.greenhouseScraper = greenhouseScraper;
        this.jobPostingRepository = jobPostingRepository;
    }


    public IngestionSummary ingestJobs(){
        int found = 0;
        int inserted = 0;
        int skipped = 0;
        List<JobSource> activeSourceList = jobSourceRepository.findByActiveStatusTrue();
        for( JobSource jobSource : activeSourceList ){
            try {
                Document htmlDocument = greenhouseScraper.scrapeJobBoard(jobSource.getCareerUrl());
                List<ParsedJob> parsedJobs = greenhouseParser.parse(htmlDocument);
                List<JobPosting> jobPostingList = new ArrayList<>();
                found += parsedJobs.size();
                for (ParsedJob parsedJob : parsedJobs) {
                    boolean exists = jobPostingRepository.existsByJobSourceAndExternalJobId(jobSource, parsedJob.externalJobId());
                    if(exists){
                        skipped++;
                        continue;
                    }
                    JobPosting jobPosting = JobPosting.builder()
                                .externalJobId(parsedJob.externalJobId())
                                .title(parsedJob.title())
                                .department(parsedJob.department())
                                .location(parsedJob.location())
                                .jobUrl(parsedJob.jobUrl())
                                .jobSource(jobSource).build();
                    jobPostingList.add(jobPosting);
                    inserted++;
                }
                jobPostingRepository.saveAll(jobPostingList);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
        return new IngestionSummary(found,inserted,skipped);
    }
}
