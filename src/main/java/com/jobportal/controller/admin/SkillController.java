package com.jobportal.controller.admin;

import com.jobportal.dto.request.NameRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Skill;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.SkillRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/skills")
@RequiredArgsConstructor
@Tag(name = "Admin - Skills", description = "Manage the master skill list (React, Java, Spring Boot...)")
public class SkillController {

    private final SkillRepository skillRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Skill>>> getAll(@RequestParam(required = false) String query) {
        List<Skill> skills = (query != null && !query.isBlank())
                ? skillRepository.findByNameContainingIgnoreCase(query)
                : skillRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Skills fetched", skills));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Skill>> create(@Valid @RequestBody NameRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Skill '" + request.getName() + "' already exists");
        }
        Skill saved = skillRepository.save(Skill.builder().name(request.getName()).build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Skill created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Skill>> update(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        skill.setName(request.getName());
        return ResponseEntity.ok(ApiResponse.success("Skill updated", skillRepository.save(skill)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted"));
    }
}
