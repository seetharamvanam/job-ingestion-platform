package com.jobingestion.jobingestionplatform.provider.greenhouse;

import com.jobingestion.jobingestionplatform.provider.JobBoardProvider;
import com.jobingestion.jobingestionplatform.provider.greenhouse.detail.GreenhouseJobDetailParser;
import com.jobingestion.jobingestionplatform.provider.greenhouse.detail.JobDetail;
import com.jobingestion.jobingestionplatform.provider.greenhouse.parser.GreenhouseParser;
import com.jobingestion.jobingestionplatform.provider.greenhouse.scraper.GreenhouseScraper;
import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.source.JobBoardProviderType;
import com.jobingestion.jobingestionplatform.source.JobSource;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GreenHouseProvider implements JobBoardProvider {

    private final GreenhouseParser greenhouseParser;
    private final GreenhouseScraper greenhouseScraper;
    private final GreenhouseJobDetailParser jobDetailParser;

    public GreenHouseProvider(GreenhouseParser greenhouseParser, GreenhouseScraper greenhouseScraper,
                              GreenhouseJobDetailParser jobDetailParser) {
        this.greenhouseParser = greenhouseParser;
        this.greenhouseScraper = greenhouseScraper;
        this.jobDetailParser = jobDetailParser;
    }

    @Override
    public List<ScrapedJob> fetchJobs(JobSource jobSource) {
        List<ScrapedJob> jobs = new ArrayList<>();
        Document firstPage = greenhouseScraper.scrapeJobBoard(jobSource.getCareerUrl());
        int totalPages = greenhouseParser.extractTotalPages(firstPage);
        jobs.addAll(greenhouseParser.parse(firstPage));
        for(int currentPage = 2; currentPage <= totalPages; currentPage++) {
            String pageUrl = jobSource.getCareerUrl() + "?page=" + currentPage;
            Document pageDocument = greenhouseScraper.scrapeJobBoard(pageUrl);
            jobs.addAll(greenhouseParser.parse(pageDocument));
        }
        return jobs;
    }

    @Override
    public ScrapedJob fetchJobDetails(ScrapedJob job) {
        Document jobPageDocument = greenhouseScraper.scrapeJobBoard(job.jobUrl());
        String jobDescription = jobDetailParser.parse(jobPageDocument);
        return new ScrapedJob(job.externalJobId(), job.title(), job.department()
        , job.location(), job.jobUrl(), jobDescription);
    }

    @Override
    public JobBoardProviderType getProviderType() {
        return JobBoardProviderType.GREENHOUSE;
    }


}
