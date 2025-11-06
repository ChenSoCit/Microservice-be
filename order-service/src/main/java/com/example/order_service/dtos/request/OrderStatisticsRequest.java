package com.example.order_service.dtos.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class OrderStatisticsRequest {
    private String type;
    private Integer month;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
}
