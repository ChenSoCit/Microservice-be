package com.example.auth_service.exception;

import java.time.OffsetDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;

@ControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
            .map(cv -> cv.getMessage())
            .findFirst()
            .orElse("Constraint violations");

        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(message)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Malformed JSON request")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException ex, HttpServletRequest request) {
        int status = ex.status() > 0 ? ex.status() : 502;
        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(status)
            .error(HttpStatus.valueOf(Math.min(status, 599)).getReasonPhrase())
            .message("Remote service error: " + ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        int status = ex.getStatusCode() != null ? ex.getStatusCode().value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        String reason = HttpStatus.resolve(status) != null ? HttpStatus.resolve(status).getReasonPhrase() : ex.getMessage();
        ErrorResponse body = ErrorResponse.builder()
            .timestamp(OffsetDateTime.now())
            .status(status)
            .error(reason)
            .message(ex.getReason() != null ? ex.getReason() : ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(status).body(body);
    }

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
}
