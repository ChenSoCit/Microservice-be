package com.example.client_server.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.client_server.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    // Xử lý lỗi 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("AuthenticationException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Xử lý lỗi 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("AccessDeniedException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}