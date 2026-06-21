package com.jobingestion.jobingestionplatform.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GreenhouseParser implements JobBoardParser {

    @Override
    public List<ParsedJob> parse(Document document) {
        List<ParsedJob> parsedJobs = new ArrayList<>();

        for (Element departmentSection : document.select("div.job-posts--table--department")){
            String department = departmentSection.selectFirst("h3.section-header") != null
                    ? departmentSection.selectFirst("h3.section-header").text() : "Unknown";

            for(Element jobRow : departmentSection.select("tr.job-post")){
                Element link = jobRow.selectFirst("a[href]");

                if(link == null){continue;}

                String jobUrl = link.attr("href");
                Long externalJobId = extractJobId(jobUrl);

                String title = link.selectFirst("p.body--medium") != null
                        ? link.selectFirst("p.body--medium").text() : "";

                String location = link.selectFirst("p.body__secondary.body--metadata") != null
                        ? link.selectFirst("p.body__secondary.body--metadata").text() : "";

                parsedJobs.add(new ParsedJob(
                        externalJobId,
                        title,
                        department,
                        location,
                        jobUrl
                ));
            }
        }
        return  parsedJobs;
    }

    private Long extractJobId(String jobUrl){
        String[] parts = jobUrl.split("/");
        return Long.parseLong(parts[parts.length-1]);
    }
}
