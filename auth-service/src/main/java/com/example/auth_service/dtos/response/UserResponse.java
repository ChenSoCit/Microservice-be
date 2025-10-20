package com.example.auth_service.dtos.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
