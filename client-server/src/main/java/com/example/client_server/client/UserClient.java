package com.example.client_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.request.UserRequest;
import com.example.client_server.dto.response.UserResponse;
import com.example.client_server.dto.response.UserWithOrderResponse;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserClient {
    @GetMapping("/{id}")
    UserResponse getUser(@PathVariable("id") int id);

    @GetMapping("/by-username/{username}")
    UserResponse findByName(@PathVariable("username") String username);

    @GetMapping("/{userId}/orders")
    ApiResponse<UserWithOrderResponse> getUserOrders(@PathVariable("userId") long userId);

    @DeleteMapping("/{id}")
    String deleteUser(@PathVariable("id") long id);

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@PathVariable("id") long id, @RequestBody UserRequest request);
}
