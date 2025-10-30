package com.example.order_service.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsResponse {
    private LocalDate date;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private Double percentOfWeek;
}
