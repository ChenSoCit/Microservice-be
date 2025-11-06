package com.example.client_server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class OrderStatisticsWeeklyResponse {
    private String type;
    private BigDecimal totalAmount;
    private Integer totalOrders;
    private List<Object> details;
}
