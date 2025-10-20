package com.example.client_server.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CntOrderResponse {
    @JsonProperty("total_order")
    private Integer totalOrder;

    @JsonProperty("status_pending")
    private Integer statusPending;

    @JsonProperty("status_delivering")
    private Integer statusDelivering;

    @JsonProperty("status_completed")
    private Integer statusCompleted;

    @JsonProperty("total_money")
    private BigDecimal totalMoney;

    private UserResponse user;
}
