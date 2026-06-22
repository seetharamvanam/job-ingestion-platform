package com.jobingestion.jobingestionplatform.ingestionrun;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionRunRepository extends JpaRepository<IngestionRun, Long> {

}
