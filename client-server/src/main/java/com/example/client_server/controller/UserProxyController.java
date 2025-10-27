package com.example.client_server.controller;

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
import com.example.client_server.dto.request.UserRequest;
import com.example.client_server.dto.response.UserResponse;
import com.example.client_server.dto.response.UserWithOrderResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
public class UserProxyController {

    private final UserClient userClient;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserResponse> getUser(HttpServletRequest request, @PathVariable("id") int id){
        String username = (String) request.getAttribute("X-Username");
        log.info("Getting user {} for request from user: {}", id, username);
        UserResponse userResponse = userClient.getUser(id);
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .data(userResponse)
                .message("User fetched successfully")
                .build();
    }

    @GetMapping("/{userId}/orders")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserWithOrderResponse> getUserOrders(@PathVariable("userId") long userId){
        return userClient.getUserOrders(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable int id){
        UserResponse existingUser = userClient.getUser(id);
        if(existingUser == null){
            log.error("User not found with id: {}", id);
            throw new RuntimeException("User not found");
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message("User deleted successfully")
                .data("deleted")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") int id, @RequestBody UserRequest request){
        UserResponse exitingUser = userClient.getUser(id);
        if(exitingUser == null){
            log.error("User not found with id: {}", id);
            throw new RuntimeException("User not found");
        }
        return userClient.updateUser(id, request);
    }

}
