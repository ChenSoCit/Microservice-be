package com.example.client_server.dto.request;

import java.time.LocalDate;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
   
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;
    private Integer roleId;
}
