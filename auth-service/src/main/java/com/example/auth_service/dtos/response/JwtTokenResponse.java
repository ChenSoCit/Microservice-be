package com.example.auth_service.dtos.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    // keep existing name used elsewhere in project
    private Date exporationTime;
}
