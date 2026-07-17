package com.jobportal.service;

import com.jobportal.dto.request.LoginRequest;
import com.jobportal.dto.request.RegisterRequest;
import com.jobportal.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    JwtResponse refreshToken(String refreshToken);
}
