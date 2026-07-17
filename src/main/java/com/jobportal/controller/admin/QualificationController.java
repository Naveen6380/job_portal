package com.jobportal.controller.admin;

import com.jobportal.dto.request.NameRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Qualification;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.QualificationRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/qualifications")
@RequiredArgsConstructor
@Tag(name = "Admin - Qualifications", description = "Manage qualification master list (B.E, MCA, MBA...)")
public class QualificationController {

    private final QualificationRepository qualificationRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Qualification>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Qualifications fetched", qualificationRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Qualification>> create(@Valid @RequestBody NameRequest request) {
        if (qualificationRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Qualification '" + request.getName() + "' already exists");
        }
        Qualification saved = qualificationRepository.save(Qualification.builder().name(request.getName()).build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Qualification created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Qualification>> update(@PathVariable Long id, @Valid @RequestBody NameRequest request) {
        Qualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + id));
        qualification.setName(request.getName());
        return ResponseEntity.ok(ApiResponse.success("Qualification updated", qualificationRepository.save(qualification)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!qualificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Qualification not found with id: " + id);
        }
        qualificationRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Qualification deleted"));
    }
}
