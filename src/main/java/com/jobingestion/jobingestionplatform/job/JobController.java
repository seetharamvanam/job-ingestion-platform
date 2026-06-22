package com.jobingestion.jobingestionplatform.job;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @GetMapping
    public ResponseEntity<JobPageResponse> searchJobs(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        return new ResponseEntity<>(jobService.searchForJobs(page, size), HttpStatus.OK);
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
