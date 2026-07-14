package com.jobingestion.jobingestionplatform.provider.lever;

import com.jobingestion.jobingestionplatform.provider.JobBoardProvider;
import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.source.JobBoardProviderType;
import com.jobingestion.jobingestionplatform.source.JobSource;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeverProvider implements JobBoardProvider {

    private final LeverScraper scraper;
    private final LeverParser parser;
    private final LeverJobDetailParser jobDetailParser;

    public LeverProvider(LeverScraper leverScraper, LeverParser leverParser, LeverJobDetailParser leverJobDetailParser) {
        this.scraper = leverScraper;
        this.parser = leverParser;
        this.jobDetailParser = leverJobDetailParser;
    }

    @Override
    public List<ScrapedJob> fetchJobs(JobSource jobSource) {
        Document document = scraper.scrapeJobBoard(
                jobSource.getCareerUrl()
        );
        return parser.parse(document);
    }

    @Override
    public ScrapedJob fetchJobDetails(ScrapedJob job) {
        Document document = scraper.scrapeJobBoard(job.jobUrl());
        String description = jobDetailParser.parseDescription(document);
        return new ScrapedJob(
                job.externalJobId(),
                job.title(),
                job.department(),
                job.location(),
                job.jobUrl(),
                description
        );
    }

    @Override
    public JobBoardProviderType getProviderType() {
        return JobBoardProviderType.LEVER;
    }
}
