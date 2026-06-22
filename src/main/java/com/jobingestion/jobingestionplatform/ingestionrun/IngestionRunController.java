package com.jobingestion.jobingestionplatform.ingestionrun;

import com.jobingestion.jobingestionplatform.ingestion.IngestionSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingestion-runs")
public class IngestionRunController {

    private final IngestionRunService ingestionRunService;
    public IngestionRunController(IngestionRunService ingestionRunService) {
        this.ingestionRunService = ingestionRunService;
    }

    @GetMapping
    public ResponseEntity<List<IngestionRunDetails>> getIngestionRuns() {
        List<IngestionRunDetails> list = ingestionRunService.searchAllIngestionRuns();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngestionRunDetails> getIngestionRunById(@PathVariable Long id) {
        IngestionRunDetails runDetails = ingestionRunService.searchIngestionRun(id);
        if(runDetails != null){
            return new ResponseEntity<>(runDetails, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
