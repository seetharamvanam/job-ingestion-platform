package com.jobingestion.jobingestionplatform.source;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSourceRepository extends JpaRepository<JobSource, Long> {

    Optional<JobSource> findByCareerUrl(String careerUrl);
    JobSource findByActiveStatusTrue();
}
