package com.example.client_server.dto.response;



import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Date exporationTime;
}
