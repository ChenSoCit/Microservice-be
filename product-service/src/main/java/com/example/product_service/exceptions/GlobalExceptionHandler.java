package com.example.product_service.exceptions;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Unexpected error: " + ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // 5. Validation error (ví dụ @Valid trong DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult br = ex.getBindingResult();
        String message = br.getFieldErrors().stream()
            .map(fe -> fe.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");

        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(message)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 1. Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
