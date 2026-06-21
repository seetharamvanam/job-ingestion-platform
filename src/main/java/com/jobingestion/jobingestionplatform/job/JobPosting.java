package com.jobingestion.jobingestionplatform.job;

import com.jobingestion.jobingestionplatform.source.JobSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "job_postings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "job_source_id", "external_job_id"
                })
})
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_job_id", nullable = false)
    private Long externalJobId;


    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_source_id", nullable = false)
    private JobSource jobSource;

    @Column
    private String title;

    @Column
    private String department;

    @Column
    private String location;

    @Column
    private String jobUrl;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column
    private LocalDateTime discoveredAt;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.discoveredAt == null) {
            this.discoveredAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
