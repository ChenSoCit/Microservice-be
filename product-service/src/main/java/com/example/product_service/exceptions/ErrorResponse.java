package com.example.product_service.exceptions;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
