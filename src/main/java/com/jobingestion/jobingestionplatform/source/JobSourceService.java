package com.jobingestion.jobingestionplatform.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JobSourceService {

    private final JobSourceRepository jobSourceRepository;
    public JobSourceService(JobSourceRepository jobSourceRepository) {
        this.jobSourceRepository = jobSourceRepository;
    }

    public void createJobSource(List<JobSourceConfig> jobSource){
        List<JobSource> newJobSources = new ArrayList<>();
        for (JobSourceConfig job : jobSource) {
            Optional<JobSource> existingJobSource = jobSourceRepository.findByCareerUrl(job.careerUrl());
            if (existingJobSource.isEmpty()) {
                JobSource jobSourceEntity = JobSource.builder()
                        .companyName(job.companyName())
                        .activeStatus(job.active())
                        .careerUrl(job.careerUrl()).build();
                newJobSources.add(jobSourceEntity);
            }else{
                log.info("Job Source already exists", job.companyName(), job.careerUrl());
            }
        }

        jobSourceRepository.saveAll(newJobSources);
    }

    public List<JobSourceConfig> fetchAllJobSources(){
        List<JobSource> jobSources = jobSourceRepository.findAll();
        List<JobSourceConfig> jobSourceConfigs = new ArrayList<>();
        for (JobSource jobSource : jobSources) {
            jobSourceConfigs.add(new JobSourceConfig(jobSource.getCompanyName(),
                    jobSource.getCareerUrl(), jobSource.getActiveStatus()));
        }
        return jobSourceConfigs;
    }

    public void setActiveStatus(String companyName){
        JobSource jobSource = jobSourceRepository.findByCompanyNameAndActiveStatusTrue(companyName);
        jobSource.setActiveStatus(false);
        jobSourceRepository.save(jobSource);
    }
}
