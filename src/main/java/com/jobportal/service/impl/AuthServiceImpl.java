package com.jobportal.service.impl;

import com.jobportal.dto.request.LoginRequest;
import com.jobportal.dto.request.RegisterRequest;
import com.jobportal.dto.response.JwtResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.RoleRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtil;
import com.jobportal.security.UserPrincipal;
import com.jobportal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        Role role = roleRepository.findByName("ROLE_" + request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRole()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .isActive(true)
                .isEmailVerified(false)   // flip to true after OTP verification flow
                .build();

        user = userRepository.save(user);

        // If registering as an Employer, auto-create a pending Company profile
        if ("EMPLOYER".equals(request.getRole())) {
            Company company = Company.builder()
                    .user(user)
                    .companyName(request.getCompanyName() != null ? request.getCompanyName() : request.getFullName())
                    .verificationStatus("PENDING")
                    .build();
            companyRepository.save(company);
        }

        // TODO: trigger OTP email here (EmailService.sendOtp) - wired in Day 4

        return buildAuthResponse(user);
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token is invalid or expired. Please login again");
        }
        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        String newAccessToken = jwtUtil.generateAccessToken(authentication);

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)  // reuse until expiry; rotate if you want stricter security
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }

    private JwtResponse buildAuthResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }
}
