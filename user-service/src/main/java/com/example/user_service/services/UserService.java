package com.example.user_service.services;

import com.example.user_service.dtos.request.UserRequest;
import com.example.user_service.dtos.common_dto.UserResponse;
import com.example.user_service.dtos.common_dto.UserWithOrderResponse;


public interface UserService {
    UserResponse createUser(UserRequest request);

    UserResponse getUserById(int id);

    UserResponse updateUser(UserRequest request, Integer userId);

    String checkRole(int id);

    int deleteUser(int id);

    UserWithOrderResponse getUserWithOrder(Integer id);

    UserResponse getByUserName(String username);
}
