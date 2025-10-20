package com.example.client_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.UserRequest;
import com.example.client_server.dto.UserResponse;
import com.example.client_server.dto.UserWithOrderResponse;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserClient {
    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUser(@PathVariable("id") long id);

    @GetMapping("/{userId}/orders")
    ApiResponse<UserWithOrderResponse> getUserOrders(@PathVariable("userId") long userId);

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteUser(@PathVariable("id") long id);

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@PathVariable("id") long id, @RequestBody UserRequest request);
}
