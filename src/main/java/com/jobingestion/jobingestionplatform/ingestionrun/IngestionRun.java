package com.jobingestion.jobingestionplatform.ingestionrun;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "ingestion_run")
public class IngestionRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private LocalDateTime startedAt;
    @Column
    private LocalDateTime completedAt;
    @Column
    @Enumerated(EnumType.STRING)
    private IngestionRunStatus status;
    @Column
    private int jobsFound;
    @Column
    private int jobsInserted;
    @Column
    private int jobsSkipped;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
