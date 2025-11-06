package com.example.order_service.dtos.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatisticsResponse {
    private String type;

    private BigDecimal totalAmount;

    private Integer totalOrders;

    private List<?> details;
}
