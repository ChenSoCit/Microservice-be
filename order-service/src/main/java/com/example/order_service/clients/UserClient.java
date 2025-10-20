package com.example.order_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.order_service.dtos.response.UserResponse;

@FeignClient(name = "user-service", path="/api/internal/users")
public interface UserClient {
    
    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Integer id);

    @GetMapping("/{id}")
    String checkRole(@PathVariable("id") Integer id);

    @DeleteMapping("/{id}")
    Integer deleteUser(Integer id);

}
