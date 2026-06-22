package com.jobingestion.jobingestionplatform.job;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @GetMapping
    public ResponseEntity<List<JobSummaryResponse>> searchJobs() {
        return new ResponseEntity<>(jobService.searchForJobs(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDetailsResponse> searchForJobDetails(@PathVariable Long id) {
        JobDetailsResponse jobDetails = jobService.searchForJobDetails(id);
        if (jobDetails != null) {
            return new ResponseEntity<>(jobDetails, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
