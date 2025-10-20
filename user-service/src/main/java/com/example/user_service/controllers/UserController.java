package com.example.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dtos.request.UserRequest;
import com.example.user_service.dtos.response.ApiResponse;
import com.example.user_service.dtos.response.UserResponse;
import com.example.user_service.dtos.response.UserWithOrderResponse;
import com.example.user_service.services.UserService;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j(topic = "USER_CONTROLLER")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable int id) {
        log.info("Received request to get user by ID: {}", id);

        UserResponse user = userService.getUserById(id);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
            .code(HttpStatus.OK.value())
            .message("get user detail by id")    
            .data(user)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<ApiResponse<UserWithOrderResponse>> getUserWithOrder(@PathVariable("userId") int userId){
        UserWithOrderResponse res1 = userService.getUserWithOrder(userId);
        ApiResponse<UserWithOrderResponse> response = ApiResponse.<UserWithOrderResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Get user with order by userId "+ userId)
            .data(res1)
            .build();
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable int id) {
        log.info("Received request to delete user by ID: {}", id);
        userService.deleteUser(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
            .code(HttpStatus.OK.value())
            .message("delete user by id")
            .data("Delete user by id: {} successful"+id)
            .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable("userId") int userId
                                                    , @RequestBody UserRequest request){
        log.info("Update user for request: {}", request);
        UserResponse user = userService.updateUser(request, userId);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Update user with id: "+userId)
            .data(user)
            .build();

        return ResponseEntity.ok(response);
    }
}
