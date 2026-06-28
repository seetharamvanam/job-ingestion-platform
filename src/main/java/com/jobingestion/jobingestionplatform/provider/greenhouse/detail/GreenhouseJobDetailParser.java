package com.jobingestion.jobingestionplatform.provider.greenhouse.detail;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class GreenhouseJobDetailParser implements JobDetailParser{

    @Override
    public String parse(Document document) {
        Element description = document.selectFirst("div.job__description.body");
        if(!(description == null)){
            return description.text();
        }
        return "";
    }
}
