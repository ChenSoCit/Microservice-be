package com.example.auth_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldValidationError {
    private String field;
    private String rejectedValue;
    private String message;
}
