package com.example.client_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.client_server.dto.VerifyTokenResponse;


@FeignClient(name = "auth-service", path= "/api/v1/auth")
public interface AuthClient {
    @GetMapping("/verify")
    VerifyTokenResponse  verifyToken(@RequestHeader("Authorization") String token);
}
