package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "experience_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String label;          // "Fresher", "1-3 Years"...

    @Column(name = "min_years")
    @Builder.Default
    private Integer minYears = 0;

    @Column(name = "max_years")
    @Builder.Default
    private Integer maxYears = 0;
}
