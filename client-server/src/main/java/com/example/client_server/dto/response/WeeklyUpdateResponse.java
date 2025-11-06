package com.example.client_server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyUpdateResponse {
    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("end_date")
    private LocalDate endDate;
    @JsonProperty("order_count")
    private Integer orderCount;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    @JsonProperty("percent_of_month")
    private Double percentOfMonth;
}
