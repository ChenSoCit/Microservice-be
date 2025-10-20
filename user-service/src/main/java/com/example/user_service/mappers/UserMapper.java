package com.example.user_service.mappers;

import java.util.List;

import com.example.user_service.dtos.response.UserLoginResponse;
import com.example.user_service.dtos.response.UserResponse;
import com.example.user_service.models.User;


public interface UserMapper {
    int createUser(User row); 

    UserResponse getUserById(int id);

    List<UserResponse> getUser();

    int updateUser(User row);

    int deleteUser(int id);

    UserLoginResponse getByUserName(String username);
}
