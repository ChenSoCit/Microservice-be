package com.example.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dtos.request.UserRequest;
import com.example.user_service.dtos.response.ApiResponse;
import com.example.user_service.dtos.common_dto.UserResponse;
import com.example.user_service.dtos.common_dto.UserWithOrderResponse;
import com.example.user_service.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j(topic = "USER_CONTROLLER")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable int id) {
        log.info("Received request to get user by ID: {}", id);
        return userService.getUserById(id);
    }

    @PostMapping("")
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        log.info("Received request to create user: {}", request);
        return userService.createUser(request);
    }

    @GetMapping("/by-username/{userName}")
    public UserResponse getUserByName(@PathVariable String userName) {
        log.info("Received request to get user by username: {}", userName);
        return userService.getByUserName(userName);
    }

    @GetMapping("/check-role/{userId}")
    public String checkRole(@PathVariable int userId) {
        log.info("Received request to check role by user ID: {}", userId);
        return userService.checkRole(userId);
    }   

    @GetMapping("/{userId}/orders")
    public UserWithOrderResponse getUserWithOrder(@PathVariable("userId") int userId){
        log.info("Received request to get orders by user ID: {}", userId);
        return userService.getUserWithOrder(userId);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        log.info("Received request to delete user by ID: {}", id);
        userService.deleteUser(id);
        return "deleted user";
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable("userId") int userId
                                 , @RequestBody UserRequest request){
        log.info("Update user for request: {}", request);
        return userService.updateUser(request, userId);
    }
}
