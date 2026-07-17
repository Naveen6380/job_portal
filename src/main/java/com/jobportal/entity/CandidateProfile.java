package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String headline;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_level_id")
    private ExperienceLevel experienceLevel;

    @Column(name = "current_ctc", precision = 10, scale = 2)
    private BigDecimal currentCtc;

    @Column(name = "expected_ctc", precision = 10, scale = 2)
    private BigDecimal expectedCtc;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Column(name = "profile_completion_pct")
    @Builder.Default
    private Integer profileCompletionPct = 0;

    @Column(name = "resume_score")
    @Builder.Default
    private Integer resumeScore = 0;

    @Column(name = "is_open_to_remote")
    @Builder.Default
    private Boolean isOpenToRemote = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}