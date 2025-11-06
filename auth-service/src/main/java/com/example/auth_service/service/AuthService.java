package com.example.auth_service.service;


import com.example.auth_service.dtos.common.LoginRequest;
import com.example.auth_service.dtos.request.RefreshTokenRequest;
import com.example.auth_service.dtos.common.UserRegisterRequest;
import com.example.auth_service.dtos.common.JwtTokenResponse;
import com.example.auth_service.dtos.common.UserResponse;

public interface AuthService {
    JwtTokenResponse login(LoginRequest request);

    JwtTokenResponse refreshToken(RefreshTokenRequest request);

    UserResponse register(UserRegisterRequest request);
}
