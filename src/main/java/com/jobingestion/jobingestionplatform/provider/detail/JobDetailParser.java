package com.jobingestion.jobingestionplatform.provider.detail;

import org.jsoup.nodes.Document;

public interface JobDetailParser {

    String parseDescription(Document document);
}
