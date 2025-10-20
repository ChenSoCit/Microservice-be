package com.example.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.user_service.dtos.request.UserRequest;

import com.example.user_service.dtos.response.UserLoginResponse;
import com.example.user_service.dtos.response.UserResponse;
import com.example.user_service.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/internal/users")
@Slf4j(topic="CONTROLLER-INTERNAL")
public class UserInternalController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.info("Received request to create user: {}", request);
        UserResponse newUser = userService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserLoginResponse> getByUserName(@PathVariable("username") String username){
        UserLoginResponse user = userService.getByUserName(username);
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(user);
    }

    @GetMapping("/check-role/{userId}")
    public ResponseEntity<String> checkRole(@PathVariable int userId) {
        log.info("Received request to check role by user ID: {}", userId);
        String roleName = userService.checkRole(userId);
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(roleName);
    }   

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        log.info("Received request to get user by ID: {}", id);

        UserResponse user = userService.getUserById(id);
        
        return ResponseEntity.ok(user);
    }
}
