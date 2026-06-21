package com.jobingestion.jobingestionplatform.scraper;

import org.jsoup.nodes.Document;

public interface JobBoardScraper {
    Document scrapeJobBoard(String careerUrl);
}
