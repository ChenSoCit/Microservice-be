package com.example.client_server.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class OrderStatisticsRequest {
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
}
