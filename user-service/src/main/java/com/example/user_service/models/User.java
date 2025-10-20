package com.example.user_service.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private Integer id;

    private String firstName;
    
    private String lastName;

    private String email;
    
    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private String phone;

    private Integer roleId;

    private String password;

    private String userName;

}
