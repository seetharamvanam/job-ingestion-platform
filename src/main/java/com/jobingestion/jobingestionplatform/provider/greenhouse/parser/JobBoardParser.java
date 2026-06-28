package com.jobingestion.jobingestionplatform.provider.greenhouse.parser;

import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import org.jsoup.nodes.Document;

import java.util.List;

public interface JobBoardParser {

    List<ScrapedJob> parse(Document document);
    int extractTotalPages(Document document);
}
