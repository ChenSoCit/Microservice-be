package com.example.auth_service.service.impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth_service.client.UserClient;
import com.example.auth_service.components.JwtTokenUtil;
import com.example.auth_service.dtos.request.LoginRequest;
import com.example.auth_service.dtos.request.RefreshTokenRequest;
import com.example.auth_service.dtos.request.UserRegisterRequest;
import com.example.auth_service.dtos.response.JwtTokenResponse;
import com.example.auth_service.dtos.response.UserLoginResponse;
import com.example.auth_service.dtos.response.UserResponse;
import com.example.auth_service.exception.ResourceNotFoundException;
import com.example.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic="AUTH SERVICE")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public JwtTokenResponse login(LoginRequest request) {
        UserLoginResponse user = userClient.getByUserName(request.getUserName());

        if(user == null || !passwordEncoder.matches(request.getPassword(), user.getPassWord())){
            throw new com.example.auth_service.exception.BadRequestException("Invalid username or password");
        }

        // tao claims
        Map<String, Object> claims = new HashMap<>();
        String roleName  = userClient.checkRole(user.getId());
        claims.put("role", roleName);

        String accessToken = jwtTokenUtil.generateAccessToken(claims, user.getUserName());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getUserName());

        return JwtTokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .exporationTime(jwtTokenUtil.getAccessTokenExpiryDate())
            .build();
    }


    @Override
    public UserResponse register(UserRegisterRequest request) {
        
        // ma hoa password
        String password = request.getPassword();
        String encodePassword =  passwordEncoder.encode(password);

        UserRegisterRequest req = UserRegisterRequest.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .address(request.getAddress())
            .phoneNumber(request.getPhoneNumber())
            .roleId(request.getRoleId())
            .password(encodePassword)
            .userName(request.getUserName())
        .build();

        
        UserResponse response = userClient.create(req);
        return response;
    }

    @Override
    public JwtTokenResponse refreshToken(RefreshTokenRequest request) {
        String Token = request.getRefreshToken();
        
        if(!jwtTokenUtil.validateToken(Token)){
            throw new com.example.auth_service.exception.BadRequestException("Invalid refresh token");
        }

        String username = jwtTokenUtil.getUsernameFromToken(Token);
        log.info("username: {}", username);
        UserLoginResponse user = userClient.getByUserName(username);
        

        if(user == null){
            throw new ResourceNotFoundException("User not found");
        }

        Map<String, Object> claims = new HashMap<>();
        String roleName  = userClient.checkRole(user.getId());
        claims.put("role", roleName);

        String newAccessToken = jwtTokenUtil.generateAccessToken(claims, username);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(username);

        return JwtTokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .tokenType("Bearer")
        .exporationTime(jwtTokenUtil.getAccessTokenExpiryDate())
        .build();
    }

}
