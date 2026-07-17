package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "posted_by", nullable = false)
    private Long postedBy;   // users.id of employer/admin/employee who posted it

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_level_id")
    private ExperienceLevel experienceLevel;

    @Column(name = "min_salary", precision = 10, scale = 2)
    private BigDecimal minSalary;

    @Column(name = "max_salary", precision = 10, scale = 2)
    private BigDecimal maxSalary;

    @Column(name = "job_type", length = 20)
    @Builder.Default
    private String jobType = "FULL_TIME";   // FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT

    @Column(name = "work_mode", length = 20)
    @Builder.Default
    private String workMode = "ONSITE";     // ONSITE, REMOTE, HYBRID

    @Column(nullable = false)
    @Builder.Default
    private Integer vacancies = 1;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";       // ACTIVE, INACTIVE, EXPIRED, CLOSED

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "views_count")
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(name = "expires_at")
    private LocalDate expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
