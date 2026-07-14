package com.jobingestion.jobingestionplatform.provider.lever;

import com.jobingestion.jobingestionplatform.provider.detail.JobDetailParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class LeverJobDetailParser implements JobDetailParser {
    @Override
    public String parseDescription(Document document) {

        Element jobDescriptionElement = document.selectFirst("[data-qa=job-description]");
        if(jobDescriptionElement == null){
            return "";
        }
        return jobDescriptionElement.text().trim();
    }
}
