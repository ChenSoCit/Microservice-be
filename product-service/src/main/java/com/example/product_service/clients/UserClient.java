package com.example.product_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.product_service.dtos.response.UserResponse;

@FeignClient(name = "user-service", path="/api/v1/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Integer id);
    
    @GetMapping("/check-role/{id}")
    String checkRole(@PathVariable("id") Integer id);
} 
