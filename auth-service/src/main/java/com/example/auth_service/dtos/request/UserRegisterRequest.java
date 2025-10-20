package com.example.auth_service.dtos.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "usename is required")
    private String userName;

    @NotNull(message = "password must be not null")
    @Size(min = 5, message = "Password minimum 5 characters " )
    private String password;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private String gender;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "phone number is invalid")
    private String phoneNumber;

    @NotNull(message = "Address is required")
    private String address;

    private Integer roleId;
}
