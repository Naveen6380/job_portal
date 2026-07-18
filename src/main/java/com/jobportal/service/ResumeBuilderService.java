package com.jobportal.service;

import com.jobportal.dto.request.EducationRequest;
import com.jobportal.dto.request.ExperienceRequest;
import com.jobportal.dto.request.ProjectRequest;
import com.jobportal.entity.Education;
import com.jobportal.entity.Experience;
import com.jobportal.entity.Project;

import java.util.List;

public interface ResumeBuilderService {

    // Education
    Education addEducation(Long userId, EducationRequest request);
    List<Education> getEducation(Long userId);
    void deleteEducation(Long userId, Long educationId);

    // Experience
    Experience addExperience(Long userId, ExperienceRequest request);
    List<Experience> getExperience(Long userId);
    void deleteExperience(Long userId, Long experienceId);

    // Projects
    Project addProject(Long userId, ProjectRequest request);
    List<Project> getProjects(Long userId);
    void deleteProject(Long userId, Long projectId);
}