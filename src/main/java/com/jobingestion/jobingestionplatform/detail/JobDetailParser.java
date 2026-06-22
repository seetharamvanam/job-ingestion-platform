package com.jobingestion.jobingestionplatform.detail;

import org.jsoup.nodes.Document;

public interface JobDetailParser {

    JobDetail parse(Document document);
}
