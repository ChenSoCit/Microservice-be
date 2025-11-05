package com.example.client_server.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class OrderStatisticsWeeklyResponse {
    private String type;
    private BigDecimal totalAmount;
    private List<WeeklyStatsResponse> details;
}
