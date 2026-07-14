package com.jobingestion.jobingestionplatform.lever;

import com.jobingestion.jobingestionplatform.provider.lever.LeverJobDetailParser;
import com.jobingestion.jobingestionplatform.provider.lever.LeverParser;
import com.jobingestion.jobingestionplatform.provider.lever.LeverProvider;
import com.jobingestion.jobingestionplatform.provider.lever.LeverScraper;
import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.source.JobBoardProviderType;
import com.jobingestion.jobingestionplatform.source.JobSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeverProviderTest {

    @Mock
    private LeverScraper scraper;

    @Mock
    private LeverParser parser;

    @Mock
    private LeverJobDetailParser jobDetailParser;

    private LeverProvider provider;

    @BeforeEach
    void setUp() {
        provider = new LeverProvider(
                scraper,
                parser,
                jobDetailParser
        );
    }

    @Test
    void shouldFetchJobsFromJobSource() {
        JobSource source = JobSource.builder()
                .companyName("Grid")
                .careerUrl("https://jobs.lever.co/Grid")
                .activeStatus(true)
                .provider(JobBoardProviderType.LEVER)
                .build();

        Document listingDocument = Jsoup.parse(
                "<div class=\"posting\"></div>"
        );

        List<ScrapedJob> expectedJobs = List.of(
                new ScrapedJob(
                        "job-123",
                        "Software Engineer",
                        "Engineering",
                        "Seattle, Washington",
                        "https://jobs.lever.co/Grid/job-123",
                        ""
                )
        );

        when(scraper.scrapeJobBoard(source.getCareerUrl()))
                .thenReturn(listingDocument);

        when(parser.parse(listingDocument))
                .thenReturn(expectedJobs);

        List<ScrapedJob> actualJobs =
                provider.fetchJobs(source);

        assertEquals(expectedJobs, actualJobs);

        verify(scraper).scrapeJobBoard(
                "https://jobs.lever.co/Grid"
        );
        verify(parser).parse(listingDocument);
    }

    @Test
    void shouldFetchAndAddJobDescription() {
        ScrapedJob originalJob = new ScrapedJob(
                "job-456",
                "Backend Engineer",
                "Engineering",
                "Remote",
                "https://jobs.lever.co/Grid/job-456",
                ""
        );

        Document detailDocument = Jsoup.parse(
                "<div data-qa=\"job-description\">" +
                        "Build backend systems" +
                        "</div>"
        );

        when(scraper.scrapeJobBoard(originalJob.jobUrl()))
                .thenReturn(detailDocument);

        when(jobDetailParser.parseDescription(detailDocument))
                .thenReturn("Build backend systems");

        ScrapedJob enrichedJob =
                provider.fetchJobDetails(originalJob);

        assertEquals(
                originalJob.externalJobId(),
                enrichedJob.externalJobId()
        );
        assertEquals(
                originalJob.title(),
                enrichedJob.title()
        );
        assertEquals(
                originalJob.department(),
                enrichedJob.department()
        );
        assertEquals(
                originalJob.location(),
                enrichedJob.location()
        );
        assertEquals(
                originalJob.jobUrl(),
                enrichedJob.jobUrl()
        );
        assertEquals(
                "Build backend systems",
                enrichedJob.description()
        );

        verify(scraper).scrapeJobBoard(
                originalJob.jobUrl()
        );
        verify(jobDetailParser)
                .parseDescription(detailDocument);
    }

    @Test
    void shouldReturnLeverProviderType() {
        assertEquals(
                JobBoardProviderType.LEVER,
                provider.getProviderType()
        );
    }
}
