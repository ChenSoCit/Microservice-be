package com.example.client_server.controller;

import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.client_server.client.AuthClient;
import com.example.client_server.client.UserClient;
import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.response.JwtTokenResponse;
import com.example.client_server.dto.request.LoginRequest;
import com.example.client_server.dto.request.UserRegisterRequest;
import com.example.client_server.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/client/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-CONTROLLER")
public class AuthProxyController {
    private final AuthClient authClient;
    private final UserClient userClient;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody UserRegisterRequest request){ 
        // kiem tra username da ton tai
        UserResponse existingUser = userClient.findByName(request.getUserName());
        if(existingUser != null){
            log.error("Username already exists: {}", request.getUserName());
            throw new RuntimeException("Username already exists");
        }
        // dang ky user moi
        UserResponse userResponse = authClient.register(request);

        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.SC_OK)
                .data(userResponse)
                .message("User registered successfully")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<JwtTokenResponse> login(@RequestBody LoginRequest request){
        log.info("Login request: {}", request.getUserName());

        JwtTokenResponse jwtTokenResponse = authClient.login(request);
        return ApiResponse.<JwtTokenResponse>builder()
                .code(HttpStatus.SC_OK)
                .data(jwtTokenResponse)
                .message("User logged in successfully")
                .build();
    }
}
