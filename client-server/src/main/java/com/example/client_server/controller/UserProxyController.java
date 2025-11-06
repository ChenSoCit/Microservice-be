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
import com.example.client_server.exception.UnauthorizedException;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/client/users")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
public class UserProxyController {

    private final UserClient userClient;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<UserResponse> getUser(HttpServletRequest request, @PathVariable("id") int id){
        String username = (String) request.getAttribute("X-Username");
        log.info("Getting user {} for request from user: {}", id, username);
        
        try {
            UserResponse userResponse = userClient.getUser(id);
            
            // Kiểm tra quyền: user chỉ được xem thông tin của chính mình
            if (!request.isUserInRole("ADMIN") && !userResponse.getUserName().equals(username)) {
                log.error("User {} is not authorized to view user ID: {}", username, id);
                throw new UnauthorizedException("Not authorized to view this user's information");
            }
            
            log.info("Successfully retrieved user {} for request from user: {}", id, username);
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .data(userResponse)
                    .message("User fetched successfully")
                    .build();
        } catch (UnauthorizedException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign error fetching user {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching user {}: {}", id, e.getMessage());
            throw new RuntimeException("Error fetching user: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/orders")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<Object> getUserOrders(HttpServletRequest request, @PathVariable("userId") long userId){
        String username = (String) request.getAttribute("X-Username");
        log.info("Getting orders for user {} requested by: {}", userId, username);
        
        try {
            // Kiểm tra user tồn tại và lấy thông tin để check quyền
            UserResponse userResponse = userClient.getUser((int) userId);
            
            // Kiểm tra quyền
            if (!request.isUserInRole("ADMIN") && !userResponse.getUserName().equals(username)) {
                log.error("User {} is not authorized to view orders of user ID: {}", username, userId);
                throw new UnauthorizedException("Not authorized to view this user's orders");
            }
            
            UserWithOrderResponse response = userClient.getUserOrders(userId);
            log.info("Successfully retrieved orders for user {}", userId);
            return ApiResponse.builder()
                    .code(200)
                    .message("Get user with order userId " + userId)
                    .data(response)
                    .build();
            
        } catch (UnauthorizedException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign error fetching orders for user {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching orders for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching user orders: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable long id){
        log.info("Deleting user with id: {}", id);
        
        try {
            // Kiểm tra user tồn tại (sẽ throw FeignException nếu không tồn tại)
            userClient.getUser((int) id);
            
            String result = userClient.deleteUser(id);
            log.info("Successfully deleted user with id: {}", id);
            
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("User deleted successfully")
                    .data(result)
                    .build();
        } catch (FeignException e) {
            log.error("Feign error deleting user {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting user {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<Object> updateUser(HttpServletRequest httpRequest, @PathVariable("id") long id, @RequestBody UserRequest request){
        String username = (String) httpRequest.getAttribute("X-Username");
        log.info("Updating user {} requested by: {}", id, username);
        
        try {
            UserResponse existingUser = userClient.getUser((int) id);
            
            // Kiểm tra quyền: user chỉ được update thông tin của chính mình
            if (!httpRequest.isUserInRole("ADMIN") && !existingUser.getUserName().equals(username)) {
                log.error("User {} is not authorized to update user ID: {}", username, id);
                throw new UnauthorizedException("Not authorized to update this user's information");
            }
            
            UserResponse response = userClient.updateUser(id, request);
            log.info("Successfully updated user {}", id);
            return ApiResponse.builder()
                    .code(200)
                    .message("Change user id: " + id)
                    .data(response)
                    .build();
        } catch (UnauthorizedException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign error updating user {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating user {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

}
