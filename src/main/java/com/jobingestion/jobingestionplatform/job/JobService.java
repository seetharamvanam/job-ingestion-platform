package com.jobingestion.jobingestionplatform.job;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobPostingRepository jobPostingRepository;

    public JobService(JobPostingRepository jobPostingRepository) {
        this.jobPostingRepository = jobPostingRepository;
    }

    public List<JobSummaryResponse> searchForJobs() {
        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        List<JobSummaryResponse> jobSummaryResponseList = new ArrayList<>();
        for (JobPosting jobPosting : jobPostings) {
            jobSummaryResponseList.add(
                    new JobSummaryResponse(
                            jobPosting.getId(),
                            jobPosting.getTitle(),
                            jobPosting.getJobSource().getCompanyName(),
                            jobPosting.getLocation(),
                            jobPosting.getDepartment(),
                            jobPosting.getJobUrl()
                    )
            );
        }
        return jobSummaryResponseList;
    }

    public JobDetailsResponse searchForJobDetails(Long id) {
        Optional<JobPosting> jobDetail = jobPostingRepository.findById(id);
        JobDetailsResponse response = null;
        if (jobDetail.isPresent()) {
            response = new JobDetailsResponse(
                    jobDetail.get().getId(),
                    jobDetail.get().getTitle(),
                    jobDetail.get().getJobSource().getCompanyName(),
                    jobDetail.get().getLocation(),
                    jobDetail.get().getDepartment(),
                    jobDetail.get().getJobUrl(),
                    jobDetail.get().getJobDescription(),
                    jobDetail.get().getDiscoveredAt());
        }
        return response;
    }
}
