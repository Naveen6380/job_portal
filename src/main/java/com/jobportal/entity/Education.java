package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "education")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    private String degree;

    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    private String grade;
}