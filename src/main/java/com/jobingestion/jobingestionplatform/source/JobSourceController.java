package com.jobingestion.jobingestionplatform.source;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class JobSourceController {

        private final JobSourceService jobSourceService;
        public JobSourceController(JobSourceService jobSourceService) {
            this.jobSourceService = jobSourceService;
        }

        @PostMapping("sources")
        public ResponseEntity createJobSource(@RequestBody List<JobSourceConfig> jobSource) {
            if(jobSource==null || jobSource.size()==0){
                throw new RuntimeException("No job sources found");
            }
            jobSourceService.createJobSource(jobSource);
            return new ResponseEntity(HttpStatus.CREATED);
        }

        @GetMapping("sources")
        public ResponseEntity<List<JobSourceConfig>> getAllJobSources(){
            List<JobSourceConfig> allJobSources = jobSourceService.fetchAllJobSources();
            return new ResponseEntity<>(allJobSources, HttpStatus.OK);
        }



}
