package com.example.user_service.services.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.clients.OrderClient;
import com.example.user_service.dtos.common_dto.UserResponse;
import com.example.user_service.dtos.common_dto.UserWithOrderResponse;
import com.example.user_service.dtos.request.UserRequest;
import com.example.user_service.dtos.response.OrderResponse;
import com.example.user_service.exceptions.BadRequestException;
import com.example.user_service.exceptions.DatabaseException;
import com.example.user_service.exceptions.DuplicateResourceException;
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

    /* Tao moi user */
    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with username: {} and email: {}", request.getUserName(), request.getEmail());
        
        try {
            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(request.getGender())
                    .address(request.getAddress())
                    .phoneNumber(request.getPhoneNumber())
                    .roleId(request.getRoleId())
                    .password(request.getPassword())
                    .userName(request.getUserName())
                    .build();

            userMapper.createUser(user);
            
            if (user.getId() == null || user.getId() == 0) {
                log.error("Failed to create user, no ID generated");
                throw new DatabaseException("Failed to create user in database");
            }
            
            log.info("User created successfully with ID: {}", user.getId());
            
            return UserResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .gender(user.getGender())
                    .dateOfBirth(user.getDateOfBirth())
                    .address(user.getAddress())
                    .phone(user.getPhoneNumber())
                    .roleId(user.getRoleId())
                    .userName(user.getUserName())
                    .password(user.getPassword())
                    .build();
                    
        } catch (DuplicateKeyException e) {
            log.error("Duplicate entry while creating user: {}", e.getMessage());
            String message = "User already exists";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("email")) {
                    message = "Email '" + request.getEmail() + "' already exists";
                } else if (e.getMessage().contains("username") || e.getMessage().contains("user_name")) {
                    message = "Username '" + request.getUserName() + "' already exists";
                }
            }
            throw new DuplicateResourceException(message, e);
        } catch (DataAccessException e) {
            log.error("Database error while creating user: {}", e.getMessage(), e);
            throw new DatabaseException("Error occurred while creating user in database", e);
        }
    }

    /* Tim kiem user theo id */
    @Override
    @Transactional
    public UserResponse getUserById(int id) {
        log.info("Fetching user with ID: {}", id);
        
        try {
            UserResponse response = userMapper.getUserById(id);
            if (response == null) {
                log.warn("Get user not found with ID: {}", id);
                throw new ResourceNotFoundException("User not found with ID: " + id);
            }
            return response;
        } catch (DataAccessException e) {
            log.error("Database error while fetching user with ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Error occurred while fetching user from database", e);
        }
    }

    /* update user */
    @Override
    @Transactional
    public UserResponse updateUser(UserRequest request, Integer userId) {
        log.info("Updating user with ID: {}", userId);
        
        try {
            // Check if user exists first
            UserResponse existingUser = userMapper.getUserById(userId);
            if (existingUser == null) {
                log.warn("Cannot update: User not found with ID: {}", userId);
                throw new ResourceNotFoundException("User not found with ID: " + userId);
            }

            User user = User.builder()
                .id(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .gender(request.getGender())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword())
                .roleId(request.getRoleId())
            .build();

            int rows = userMapper.updateUser(user);
            if (rows != 1) {
                log.error("Failed to update user with ID: {}. Affected rows: {}", userId, rows);
                throw new DatabaseException("Failed to update user in database");
            }
            
            log.info("User updated successfully with ID: {}", userId);

            return  UserResponse.builder()
                    .id(userId)
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .address(user.getAddress())
                    .dateOfBirth(user.getDateOfBirth())
                    .phone(user.getPhoneNumber())
                    .roleId(user.getRoleId())
                    .userName(user.getUserName())
                    .password(user.getPassword())
                .gender(user.getGender())
            .build();
            
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw ResourceNotFoundException
        } catch (DuplicateKeyException e) {
            log.error("Duplicate entry while updating user: {}", e.getMessage());
            String message = "Cannot update: duplicate value";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("email")) {
                    message = "Email '" + request.getEmail() + "' already exists";
                } else if (e.getMessage().contains("username") || e.getMessage().contains("user_name")) {
                    message = "Username '" + request.getUserName() + "' already exists";
                }
            }
            throw new DuplicateResourceException(message, e);
        } catch (DataAccessException e) {
            log.error("Database error while updating user with ID {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Error occurred while updating user in database", e);
        }
    }

    /* Kiem tra role user */
    @Override
    @Transactional
    public String checkRole(int id) {
        log.info("Checking role for user ID: {}", id);
        
        try {
            UserResponse user = userMapper.getUserById(id);
            if (user == null) {
                log.warn("User not found with ID: {}", id);
                throw new ResourceNotFoundException("User not found with ID: " + id);
            }
            
            Role role = roleMapper.getRoleById(user.getRoleId());
            if (role == null) {
                log.error("Role not found with ID: {}", user.getRoleId());
                throw new ResourceNotFoundException("Role not found with ID: " + user.getRoleId());
            }
            
            return switch (role.getName()) {
                case "ADMIN" -> "ADMIN";
                case "USER" -> "USER";
                default -> "EMP";
            };
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw ResourceNotFoundException
        } catch (DataAccessException e) {
            log.error("Database error while checking role for user ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Error occurred while checking user role from database", e);
        }
    }

    /* Xoa user */
    @Override
    @Transactional
    public int deleteUser(int userId) {
        log.info("Deleting user with ID: {}", userId);
        
        try {
            // Check if user exists first
            UserResponse existingUser = userMapper.getUserById(userId);
            if (existingUser == null) {
                log.warn("Cannot delete: User not found with ID: {}", userId);
                throw new ResourceNotFoundException("User not found with ID: " + userId);
            }
            
            int deleteUser = userMapper.deleteUser(userId);
            if(deleteUser != 1){
                log.error("Failed to delete user with ID: {}. Affected rows: {}", userId, deleteUser);
                throw new DatabaseException("Failed to delete user from database");
            }
            
            log.info("User deleted successfully with ID: {}", userId);
            return deleteUser;
            
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw ResourceNotFoundException
        } catch (DataAccessException e) {
            log.error("Database error while deleting user with ID {}: {}", userId, e.getMessage(), e);
            throw new DatabaseException("Error occurred while deleting user from database", e);
        }
    }

    /*Thong tin user + order*/
    @Override
    @Transactional
    public UserWithOrderResponse getUserWithOrder(Integer id) {
        log.info("Fetching user with orders for ID: {}", id);
        
        try {
            UserResponse user = userMapper.getUserById(id);
            if(user == null){
                log.warn("User not found with ID: {}", id);
                throw new ResourceNotFoundException("User not found with ID: " + id);
            }

            List<OrderResponse> orders;
            try {
                orders = orderClient.getOrderByUserId(id);
                log.info("Successfully fetched {} orders for user ID: {}", orders.size(), id);
            } catch (Exception e) {
                log.warn("Failed to fetch orders for user ID: {}. Error: {}", id, e.getMessage());
                orders = Collections.emptyList();
            }

            return UserWithOrderResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .order(orders)
            .build();
            
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw ResourceNotFoundException
        } catch (DataAccessException e) {
            log.error("Database error while fetching user with orders for ID {}: {}", id, e.getMessage(), e);
            throw new DatabaseException("Error occurred while fetching user from database", e);
        }
    }

    /* Tim kiem user theo name */
    @Override
    @Transactional
    public UserResponse getByUserName(String username) {
        log.info("Fetching user with username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            log.warn("Username is null or empty");
            throw new BadRequestException("Username cannot be null or empty");
        }
        
        try {
            UserResponse response = userMapper.getByUserName(username);
            if (response == null) {
                log.warn("User not found with username: {}", username);
                throw new ResourceNotFoundException("User not found with username: " + username);
            }
            return response;
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw ResourceNotFoundException
        } catch (DataAccessException e) {
            log.error("Database error while fetching user with username {}: {}", username, e.getMessage(), e);
            throw new DatabaseException("Error occurred while fetching user from database", e);
        }
    }
}
