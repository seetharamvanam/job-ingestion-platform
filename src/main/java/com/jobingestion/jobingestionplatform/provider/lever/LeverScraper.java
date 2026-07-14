package com.jobingestion.jobingestionplatform.provider.lever;

import com.jobingestion.jobingestionplatform.provider.scraper.JobBoardScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LeverScraper implements JobBoardScraper {


    @Override
    public Document scrapeJobBoard(String careerUrl) {
        try{
            return Jsoup.connect(careerUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(15_000)
                    .get();
        }catch(IOException e){
            throw new RuntimeException(
                    "Failed to scrape Lever job board: " + careerUrl, e
            );
        }
    }
}
