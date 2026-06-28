package com.jobingestion.jobingestionplatform.job;

import com.jobingestion.jobingestionplatform.source.JobSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Boolean existsByJobSourceAndExternalJobId(JobSource jobSource, String externalJobId);
    List<JobPosting> findByJobSource(JobSource jobSource);
    Optional<JobPosting> findByExternalJobId(String externalJobId);

}
