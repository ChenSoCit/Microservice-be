package com.example.order_service.dtos.response;

import com.example.order_service.commons.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
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

    private OrderStatus status;

    @JsonProperty("total_money")
    private BigDecimal totalMoney;
}
