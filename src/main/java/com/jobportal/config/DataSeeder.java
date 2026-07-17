package com.jobportal.config;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.RoleRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bootstraps the very first Admin account so someone can log in and start
 * creating Employees / verifying Companies from Day 1.
 * Roles table is seeded via schema.sql; this only seeds the super-admin USER.
 *
 * Default login (CHANGE PASSWORD IMMEDIATELY after first login):
 *   email:    admin@jobportal.com
 *   password: Admin@123
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@jobportal.com")) {
            return; // already seeded
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN missing - did you run schema.sql seed data?"));

        User admin = User.builder()
                .fullName("Super Admin")
                .email("admin@jobportal.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(adminRole)
                .isActive(true)
                .isEmailVerified(true)
                .build();

        userRepository.save(admin);
        System.out.println(">>> Default admin seeded: admin@jobportal.com / Admin@123 (change this immediately)");
    }
}
