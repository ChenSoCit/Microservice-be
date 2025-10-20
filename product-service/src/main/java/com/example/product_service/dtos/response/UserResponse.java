package com.example.product_service.dtos.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;

    private String email;
    
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String phone;
    
    @JsonProperty("role_id")
    private Integer roleId;
    
}
