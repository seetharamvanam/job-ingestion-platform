package com.jobingestion.jobingestionplatform.provider.greenhouse.detail;

import org.jsoup.nodes.Document;

public interface JobDetailParser {

    String parse(Document document);
}
