package com.jobingestion.jobingestionplatform.provider.lever;

import com.jobingestion.jobingestionplatform.provider.model.ScrapedJob;
import com.jobingestion.jobingestionplatform.provider.parser.JobBoardParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class LeverParser implements JobBoardParser {

    @Override
    public List<ScrapedJob> parse(Document document) {
        List<ScrapedJob> jobs = new ArrayList<>();
        for(Element posting : document.select("div.posting")){

            Element link = posting.selectFirst("a.posting-title[href]");
            if(link == null) continue;

            String jobUrl = link.absUrl("href");
            if(jobUrl.isBlank()){
                jobUrl = link.attr("href");
            }

            String externalJobId = posting.attr("data-qa-posting-id");
            if(externalJobId.isBlank()){
                externalJobId = extractJobId(jobUrl);
            }

            Element titleElement = posting.selectFirst("[data-qa=posting-name]");
            String title = titleElement != null ? titleElement.text().trim() : "";
            if(title.isBlank()){
                Element fallBackTitle = posting.selectFirst("h5");
                title = fallBackTitle != null ? fallBackTitle.text().trim() : "";
            }

            Element locationElement = posting.selectFirst(".sort-by-location");
            String location = locationElement != null ? locationElement.text().trim() : "";
            String department = extractDepartment(posting);

            if(externalJobId.isBlank()
            || title.isBlank()
            || jobUrl .isBlank()){
                continue;
            }

            jobs.add(new ScrapedJob(
                    externalJobId,
                    title,
                    department,
                    location,
                    jobUrl,
                    ""
                    ));
        }
        return jobs;
    }

    private String extractJobId(String url){
        try{
            String path = URI.create(url).getPath();

            if(path == null || path.isBlank()){
                return "";
            }

            String normalizedPath = path.endsWith("/")
                    ? path.substring(0, path.length() - 1)
                    : path;

            int finalSlash = normalizedPath.lastIndexOf("/");
            return finalSlash >= 0
                    ? normalizedPath.substring(finalSlash + 1)
                    : normalizedPath;
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private String extractDepartment(Element posting){
        Element teamElement = posting.selectFirst(".sort-by-team");
        if(teamElement != null && !teamElement.text().isBlank()){
            return teamElement.text().trim();
        }
        Element groupElement = posting.closest(".postings-group");
        if(groupElement != null){
            Element groupTitle = groupElement.selectFirst(".postings-group-title");
            if(groupTitle != null && !groupTitle.text().isBlank()){
                return groupTitle.text().trim();
            }
        }
        return "Unknown";
    }
}
