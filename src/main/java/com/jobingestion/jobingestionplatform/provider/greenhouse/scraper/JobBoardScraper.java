package com.jobingestion.jobingestionplatform.provider.greenhouse.scraper;

import org.jsoup.nodes.Document;

public interface JobBoardScraper {
    Document scrapeJobBoard(String careerUrl);
}
