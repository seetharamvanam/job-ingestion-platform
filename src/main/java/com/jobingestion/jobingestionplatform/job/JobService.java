package com.jobingestion.jobingestionplatform.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public JobPageResponse searchForJobs(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<JobPosting> jobPostingPage = jobPostingRepository.findAll(pageRequest);
        Pageable pageable = jobPostingPage.getPageable();
        List<JobSummaryResponse> jobSummaryResponseList = new ArrayList<>();
        for (JobPosting jobPosting : jobPostingPage) {
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

        return new JobPageResponse(jobSummaryResponseList, pageable.getPageNumber(), pageable.getPageSize(),
                jobPostingPage.getTotalElements(), jobPostingPage.getTotalPages());
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
