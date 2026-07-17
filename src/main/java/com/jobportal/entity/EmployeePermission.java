package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "permission_key", nullable = false, length = 60)
    private String permissionKey;   // MANAGE_JOBS, MANAGE_CANDIDATES, SEND_EMAIL, SEND_SMS, VIEW_REPORTS

    @Column(name = "granted_by")
    private Long grantedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
