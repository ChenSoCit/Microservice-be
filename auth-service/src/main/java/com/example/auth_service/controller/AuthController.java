package com.example.auth_service.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.components.JwtTokenUtil;
import com.example.auth_service.dtos.common.LoginRequest;
import com.example.auth_service.dtos.request.RefreshTokenRequest;
import com.example.auth_service.dtos.common.UserRegisterRequest;
import com.example.auth_service.dtos.common.JwtTokenResponse;
import com.example.auth_service.dtos.common.UserResponse;
import com.example.auth_service.dtos.common.VerifyTokenResponse;
import com.example.auth_service.service.AuthService;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j(topic = "AUTH-CONTROLLER")
@RequiredArgsConstructor
public class AuthController {
    
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthService authService;


    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("Registering User: {}", request.getUserName());
        return  authService.register(request);
    }

    @PostMapping("/login")
    public JwtTokenResponse login(@Valid @RequestBody LoginRequest request){
        log.info("Login User: {}", request.getUserName());
        return  authService.login(request);
    }

    @PostMapping("/refresh")
    public JwtTokenResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request){

        return authService.refreshToken(request);
    }

    @GetMapping("/verify")
    public VerifyTokenResponse verifyToken(@RequestHeader("Authorization") String auth) {
        boolean isValid = jwtTokenUtil.validateToken(auth);
        if (isValid) {
            Claims claims = jwtTokenUtil.extractAllClaims(auth);
            String role = (String) claims.get("role");
            String username = jwtTokenUtil.getUsernameFromToken(auth);
            Set<String> roles = new HashSet<>();
            roles.add(role);
            return VerifyTokenResponse.builder()
                .username(username)
                .roles(roles)
                .valid(true)
                .build();
        } else {
            return VerifyTokenResponse.builder()
                .valid(false)
                .build();
        }
    }
}
