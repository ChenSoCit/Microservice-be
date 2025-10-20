package com.example.client_server.exception;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.client_server.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class FeignGlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, HttpServletRequest request) {
        log.error("FeignException: {}", ex.getMessage(), ex);
        String body = ex.contentUTF8();
        String path = request.getRequestURI();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            ErrorResponse errorResponse = mapper.readValue(body, ErrorResponse.class);
            errorResponse.setPath(path);
            log.warn("Parsed FeignException body: {}", errorResponse);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        } catch (Exception e) {
            log.error("Error parsing FeignException body: {}", body);
            return ResponseEntity.status(ex.status())
                                 .body(ErrorResponse.builder()
                                                    .timestamp(OffsetDateTime.now())
                                                    .status(ex.status())
                                                    .error(HttpStatus.valueOf(ex.status()).getReasonPhrase())
                                                    .message(ex.getMessage())
                                                    .path(path)
                                                    .build());
        }    
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage());
        String path = request.getRequestURI();
        return ResponseEntity.internalServerError().body(Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "message", ex.getMessage(),
            "path", path 
        ));
    }
}
