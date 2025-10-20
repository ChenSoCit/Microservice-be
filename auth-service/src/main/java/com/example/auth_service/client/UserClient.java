package com.example.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.auth_service.dtos.request.UserRegisterRequest;
import com.example.auth_service.dtos.response.UserLoginResponse;
import com.example.auth_service.dtos.response.UserResponse;

@FeignClient(name="user-service", path="/api/internal/users")
public interface UserClient {

    @PostMapping("")
    UserResponse create(@RequestBody UserRegisterRequest request);
    
    @GetMapping("/by-username/{username}")
    UserLoginResponse getByUserName(@PathVariable("username") String username);
    
    @GetMapping("/check-role/{userId}")
    String checkRole(@PathVariable("userId") int userId);
}
