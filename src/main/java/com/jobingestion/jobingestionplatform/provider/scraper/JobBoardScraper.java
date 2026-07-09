package com.jobingestion.jobingestionplatform.provider.scraper;

import org.jsoup.nodes.Document;

public interface JobBoardScraper {
    Document scrapeJobBoard(String careerUrl);
}
