package com.example.client_server.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class VerifyTokenResponse {
    private String username;
    private Set<String> roles;
    private boolean valid;
}
