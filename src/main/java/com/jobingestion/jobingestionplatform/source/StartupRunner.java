package com.jobingestion.jobingestionplatform.source;

import com.jobingestion.jobingestionplatform.ingestion.JobIngestionService;
import com.jobingestion.jobingestionplatform.parser.GreenhouseParser;
import com.jobingestion.jobingestionplatform.parser.ParsedJob;
import com.jobingestion.jobingestionplatform.scraper.GreenhouseScraper;
import org.jsoup.nodes.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupRunner implements CommandLineRunner {

    private final JobSourceLoader jobSourceLoader;
    private final JobIngestionService jobIngestionService;
    public StartupRunner(
            JobSourceLoader jobSourceLoader,
            JobIngestionService jobIngestionService) {
        this.jobSourceLoader = jobSourceLoader;
        this.jobIngestionService = jobIngestionService;}

    @Override
    public void run(String... args){
        List<JobSource> jobSourceList =  jobSourceLoader.loadAndSave();
        System.out.println(jobIngestionService.ingestJobs());
    }
}
