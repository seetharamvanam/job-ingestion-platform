package com.jobingestion.jobingestionplatform.detail;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class GreenhouseJobDetailParser implements JobDetailParser{

    @Override
    public JobDetail parse(Document document) {
        Element description = document.selectFirst("div.job__description.body");
        if(!(description == null)){
            return new JobDetail(description.text());
        }
        return new JobDetail("");
    }
}
