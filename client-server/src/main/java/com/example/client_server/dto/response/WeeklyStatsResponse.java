package com.example.client_server.dto.response;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStatsResponse {
    private int weekNumber;
    private int orderCount;
    private BigDecimal totalAmount;
    private Double percentOfMonth;
}