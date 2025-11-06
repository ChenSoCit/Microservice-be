package com.example.auth_service.dtos.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String userName;
    private String password;
}
