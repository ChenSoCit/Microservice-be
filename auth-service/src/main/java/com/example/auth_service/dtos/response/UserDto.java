package com.example.auth_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String Username;
    private String roles;
}
