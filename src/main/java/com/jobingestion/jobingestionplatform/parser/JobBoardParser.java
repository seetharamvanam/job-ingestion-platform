package com.jobingestion.jobingestionplatform.parser;

import org.jsoup.nodes.Document;

import java.util.List;

public interface JobBoardParser {

    List<ParsedJob> parse(Document document);
}
