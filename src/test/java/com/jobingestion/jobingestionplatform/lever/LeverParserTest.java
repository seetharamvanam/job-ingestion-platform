package com.jobingestion.jobingestionplatform.lever;

import com.jobingestion.jobingestionplatform.provider.lever.LeverParser;
import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeverParserTest {

    private LeverParser parser;

    @BeforeEach
    void setUp(){
        parser = new LeverParser();
    }


    @Test
    void shouldParseValidLeverPosting(){
        String html = """
                <html>
                    <body>
                        <div class="posting"
                             data-qa-posting-id="job-123">

                            <a class="posting-title"
                               href="https://jobs.lever.co/Grid/job-123">

                                <h5 data-qa="posting-name">
                                    Backend Software Engineer
                                </h5>

                                <div class="posting-categories">
                                    <span class="sort-by-location">
                                        Seattle, Washington
                                    </span>

                                    <span class="sort-by-team">
                                        Engineering
                                    </span>
                                </div>
                            </a>
                        </div>
                    </body>
                </html>
                """;
        Document document = Jsoup.parse(html);
        List<ScrapedJob> jobs = parser.parse(document);
        assertEquals(1, jobs.size());
        ScrapedJob job = jobs.getFirst();
        assertEquals("job-123", job.externalJobId());
        assertEquals("Backend Software Engineer", job.title());
        assertEquals("Engineering", job.department());
        assertEquals("Seattle, Washington", job.location());
        assertEquals("https://jobs.lever.co/Grid/job-123", job.jobUrl());
        assertEquals("", job.description());
    }
}
