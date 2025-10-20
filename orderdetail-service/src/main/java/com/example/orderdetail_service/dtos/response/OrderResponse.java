package com.example.orderdetail_service.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String phone;

    private String address;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    private String status;

    @JsonProperty("total_money")
    private BigDecimal totalMoney;
}
