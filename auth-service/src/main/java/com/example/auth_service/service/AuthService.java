package com.example.auth_service.service;


import com.example.auth_service.dtos.request.LoginRequest;
import com.example.auth_service.dtos.request.RefreshTokenRequest;
import com.example.auth_service.dtos.request.UserRegisterRequest;
import com.example.auth_service.dtos.response.JwtTokenResponse;
import com.example.auth_service.dtos.response.UserResponse;

public interface AuthService {
    JwtTokenResponse login(LoginRequest request);

    JwtTokenResponse refreshToken(RefreshTokenRequest request);

    UserResponse register(UserRegisterRequest request);
}
