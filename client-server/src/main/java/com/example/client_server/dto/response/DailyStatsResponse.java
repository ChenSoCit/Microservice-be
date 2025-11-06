package com.example.client_server.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsResponse {
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("order_count")
    private int orderCount;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    @JsonProperty("percent_of_week")
    private Double percentOfWeek;
}