package com.example.client_server.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data

public class OrderDetailResponse {
    @JsonProperty("order_id")
    private Integer id;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    private String status;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    private UserResponse user;
}
