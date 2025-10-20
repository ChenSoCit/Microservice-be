package com.example.user_service.services;

import com.example.user_service.dtos.request.UserRequest;
import com.example.user_service.dtos.response.UserLoginResponse;
import com.example.user_service.dtos.response.UserResponse;
import com.example.user_service.dtos.response.UserWithOrderResponse;


public interface UserService {
    UserResponse createUser(UserRequest request);

    UserResponse getUserById(int id);

    UserResponse updateUser(UserRequest request, Integer userId);

    String checkRole(int id);

    int deleteUser(int id);

    UserWithOrderResponse getUserWithOrder(Integer id);

    UserLoginResponse getByUserName(String username);
}
