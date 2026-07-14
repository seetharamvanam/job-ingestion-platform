package com.jobingestion.jobingestionplatform.lever;


import com.jobingestion.jobingestionplatform.provider.lever.LeverJobDetailParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeverJobDetailParserTest {

    private LeverJobDetailParser parser;

    @BeforeEach
    void setUp(){
        parser = new LeverJobDetailParser();
    }

    @Test
    void shouldExtractJobDescriptionWhenDescriptionExists(){
        String html = """
                <html>
                    <body>
                        <div class="section page-centered"
                             data-qa="job-description">
                            <h2>The Role</h2>
                            <p>
                                You will build reliable backend systems.
                            </p>
                            <h2>What We Require</h2>
                            <ul>
                                <li>Experience with Java</li>
                                <li>Knowledge of distributed systems</li>
                            </ul>
                        </div>
                    </body>
                </html>
                """;

        Document document = Jsoup.parse(html);
        String description = parser.parseDescription(document);
        assertTrue(description.contains("The Role"));
        assertTrue(description.contains("You will build reliable backend systems."));
        assertTrue(description.contains("Experience with Java"));
    }

    @Test
    void shouldReturnEmptyStringWhenDescriptionDoesNotExist(){
        String html = """
                <html>
                    <body>
                        <h1>Software Engineer</h1>
                    </body>
                </html>
                """;
        Document document = Jsoup.parse(html);
        String description = parser.parseDescription(document);
        assertEquals("", description);
    }

    @Test
    void shouldNotIncludeContentOutsideDescriptionElement(){
        String html = """
                <html>
                    <body>
                        <header>Grid Careers</header>

                        <div data-qa="job-description">
                            <p>Build production software.</p>
                        </div>

                        <footer>Apply for this job</footer>
                    </body>
                </html>
                """;
        Document document = Jsoup.parse(html);
        String description = parser.parseDescription(document);
        assertTrue(description.contains("Build production software."));
        assertFalse(description.contains("Grid Careers"));
        assertFalse(description.contains("Apply for this job"));
    }
}
