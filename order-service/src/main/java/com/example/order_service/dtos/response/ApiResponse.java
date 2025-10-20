package com.example.order_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
}
