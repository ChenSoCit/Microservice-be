package com.example.client_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.client_server.client.UserClient;
import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.UserRequest;
import com.example.client_server.dto.UserResponse;
import com.example.client_server.dto.UserWithOrderResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProxyController {

    private final UserClient userClient;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserResponse> getUser(@PathVariable("id") long id){
        return userClient.getUser(id);
    }

    @GetMapping("/{userId}/orders")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserWithOrderResponse> getUserOrders(@PathVariable("userId") long userId){
        return userClient.getUserOrders(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable int id){
        return userClient.deleteUser(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") long id, @RequestBody UserRequest request){
        return userClient.updateUser(id, request);
    }
       
}
