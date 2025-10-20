package com.example.user_service.services.impl;



import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.clients.OrderClient;
import com.example.user_service.dtos.request.UserRequest;
import com.example.user_service.dtos.response.OrderResponse;
import com.example.user_service.dtos.response.UserLoginResponse;
import com.example.user_service.dtos.response.UserResponse;
import com.example.user_service.dtos.response.UserWithOrderResponse;
import com.example.user_service.exceptions.BadRequestException;
import com.example.user_service.exceptions.ResourceNotFoundException;
import com.example.user_service.mappers.RoleMapper;
import com.example.user_service.mappers.UserMapper;
import com.example.user_service.models.Role;
import com.example.user_service.models.User;
import com.example.user_service.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .phone(request.getPhoneNumber())
                .roleId(request.getRoleId())
                .password(request.getPassword())
                .userName(request.getUserName())
                .build();

        userMapper.createUser(user);
        
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .address(user.getAddress())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                .build();
    }

    @Override
    public UserResponse getUserById(int id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse response = userMapper.getUserById(id);
        return response;
    }

    @Override
    public UserResponse updateUser(UserRequest request, Integer userId) {
        UserResponse extingUser = userMapper.getUserById(userId);
        if(extingUser == null){
            throw new ResourceNotFoundException("User not found with id "+userId);
        }

        User user = User.builder()
            .id(userId)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .gender(request.getGender())
            .address(request.getAddress())
            .dateOfBirth(request.getDateOfBirth())
            .phone(request.getPhoneNumber())
            .password(request.getPassword())
            .roleId(request.getRoleId())
        .build();

        int rows = userMapper.updateUser(user);
        if (rows != 1) {
            throw new BadRequestException("update user to database fail");
        }

        return  UserResponse.builder()
            .id(userId)
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .address(user.getAddress())
            .dateOfBirth(user.getDateOfBirth())
            .phone(user.getPhone())
            .roleId(user.getRoleId())
            .gender(user.getGender())
        .build();
    }

    @Override
    public String checkRole(int id) {
        UserResponse user = userMapper.getUserById(id);
        Role role = roleMapper.getRoleById(user.getRoleId());
        if(role.getName().equals("ADMIN")) {
            return "ADMIN";
        } else if (role.getName().equals("USER")) {
            return "USER";
        } else {
            return "EMP";
        }
    }

    @Override
    @Transactional
    public int deleteUser(int userId) {
        log.info("Deleting user with ID: {}", userId);

        UserResponse user = userMapper.getUserById(userId);
        if(user == null){
            throw new ResourceNotFoundException("User not found id "+userId);
        }
        int deleteUser = userMapper.deleteUser(userId);
        if(deleteUser != 1){
            throw new BadRequestException("Delete user to database fail");
        }
        return deleteUser;
    }

    @Override
    public UserWithOrderResponse getUserWithOrder(Integer id) {
        UserResponse user = userMapper.getUserById(id);
        if(user == null){
            throw new ResourceNotFoundException("User not found id "+id);
        }

        List<OrderResponse> orders;
        try {
            orders = orderClient.getOrderByUserId(id);
        } catch (Exception e) {
           orders = Collections.emptyList();
        }

        return UserWithOrderResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .order(orders)
        .build();
    }

    @Override
    public UserLoginResponse getByUserName(String username) {
        UserLoginResponse response = userMapper.getByUserName(username);
        return response;
    }
}
