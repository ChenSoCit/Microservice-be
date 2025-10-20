package com.example.auth_service.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.components.JwtTokenUtil;
import com.example.auth_service.dtos.request.LoginRequest;
import com.example.auth_service.dtos.request.RefreshTokenRequest;
import com.example.auth_service.dtos.request.UserRegisterRequest;
import com.example.auth_service.dtos.response.ApiResponse;
import com.example.auth_service.dtos.response.JwtTokenResponse;
import com.example.auth_service.dtos.response.UserResponse;
import com.example.auth_service.dtos.response.VerifyTokenResponse;
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
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse user = authService.register(request);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
            .code(HttpStatus.OK.value())
            .message("creating user successfully")
            .data(user)
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@Valid @RequestBody LoginRequest request){
        JwtTokenResponse result = authService.login(request);

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(result); 
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        JwtTokenResponse result = authService.refreshToken(request);

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(result); 
    }

    @GetMapping("/verify")
    public ResponseEntity<VerifyTokenResponse> verifyToken(@RequestHeader("Authorization") String auth) {
        boolean isValid = jwtTokenUtil.validateToken(auth);
        if (isValid) {
            Claims claims = jwtTokenUtil.extractAllClaims(auth);
            String role = (String) claims.get("role");
            String username = jwtTokenUtil.getUsernameFromToken(auth);
            Set<String> roles = new HashSet<>();
            roles.add(role);
            VerifyTokenResponse response = VerifyTokenResponse.builder()
                .username(username)
                .roles(roles)
                .valid(true)
                .build();
            return ResponseEntity.ok(response);
        } else {
            VerifyTokenResponse response = VerifyTokenResponse.builder()
                .valid(false)
                .build();
            return ResponseEntity.ok(response);
        }
    }

}
