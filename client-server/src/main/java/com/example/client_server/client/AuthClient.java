package com.example.client_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.client_server.dto.response.JwtTokenResponse;
import com.example.client_server.dto.request.LoginRequest;
import com.example.client_server.dto.request.UserRegisterRequest;
import com.example.client_server.dto.response.UserResponse;
import com.example.client_server.dto.response.VerifyTokenResponse;


@FeignClient(name = "auth-service", path= "/api/v1/auth")
public interface AuthClient {
    @GetMapping("/verify")
    VerifyTokenResponse  verifyToken(@RequestHeader("Authorization") String token);

    @PostMapping("/register")
    UserResponse register(@RequestBody UserRegisterRequest request);

    @PostMapping("/login")
    JwtTokenResponse login(@RequestBody LoginRequest request);

}
