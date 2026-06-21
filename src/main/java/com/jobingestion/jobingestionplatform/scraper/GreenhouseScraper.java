package com.jobingestion.jobingestionplatform.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GreenhouseScraper implements JobBoardScraper {

    @Override
    public Document scrapeJobBoard(String careerUrl) {
        try{
            return Jsoup.connect(careerUrl).get();
        } catch (IOException e) {
            throw new RuntimeException("Failed to scrape Job Board" + careerUrl, e);
        }
    }
}
