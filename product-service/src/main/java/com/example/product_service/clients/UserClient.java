package com.example.product_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.product_service.dtos.response.ApiResponse;
import com.example.product_service.dtos.response.UserResponse;

@FeignClient(name = "user-service", path="/api/internal/users")
public interface UserClient {

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable("id") Integer id);
    
    @GetMapping("/check-role/{id}")
    ApiResponse<String> checkRole(@PathVariable("id") Integer id);
} 
