package com.example.order_service.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class OrderStatisticsResponse {
    private String type;
    private BigDecimal totalAmount;
    private Integer orderCount;
    private List<?> details;
}
