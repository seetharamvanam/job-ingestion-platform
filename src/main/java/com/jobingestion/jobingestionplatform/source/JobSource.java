package com.jobingestion.jobingestionplatform.source;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "job_sources")
public class JobSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(columnDefinition = "text", nullable = false)
    private String careerUrl;

    @Column(nullable = false)
    private Boolean activeStatus;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobBoardProviderType provider;


    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public JobSource(String companyName, String careerUrl, Boolean active) {
        this.companyName = companyName;
        this.careerUrl = careerUrl;
        this.activeStatus = active;
    }
}
