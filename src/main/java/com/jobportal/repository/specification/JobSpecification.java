package com.jobportal.repository.specification;

import com.jobportal.entity.Job;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class JobSpecification {

    public static Specification<Job> hasKeyword(String keyword) {
        return (root, query, cb) -> keyword == null || keyword.isBlank() ? null :
                cb.or(
                        cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
                );
    }

    public static Specification<Job> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null :
                cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Job> hasLocation(Long locationId) {
        return (root, query, cb) -> locationId == null ? null :
                cb.equal(root.get("location").get("id"), locationId);
    }

    public static Specification<Job> hasCompany(Long companyId) {
        return (root, query, cb) -> companyId == null ? null :
                cb.equal(root.get("company").get("id"), companyId);
    }

    public static Specification<Job> hasQualification(Long qualificationId) {
        return (root, query, cb) -> qualificationId == null ? null :
                cb.equal(root.get("qualification").get("id"), qualificationId);
    }

    public static Specification<Job> hasExperienceLevel(Long experienceLevelId) {
        return (root, query, cb) -> experienceLevelId == null ? null :
                cb.equal(root.get("experienceLevel").get("id"), experienceLevelId);
    }

    public static Specification<Job> hasJobType(String jobType) {
        return (root, query, cb) -> jobType == null || jobType.isBlank() ? null :
                cb.equal(root.get("jobType"), jobType.toUpperCase());
    }

    public static Specification<Job> hasWorkMode(String workMode) {
        return (root, query, cb) -> workMode == null || workMode.isBlank() ? null :
                cb.equal(root.get("workMode"), workMode.toUpperCase());
    }

    public static Specification<Job> minSalaryAtLeast(BigDecimal minSalary) {
        return (root, query, cb) -> minSalary == null ? null :
                cb.greaterThanOrEqualTo(root.get("maxSalary"), minSalary);
    }

    public static Specification<Job> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), "ACTIVE");
    }
}