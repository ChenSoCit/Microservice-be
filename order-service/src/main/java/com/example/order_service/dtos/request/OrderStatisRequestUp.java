package com.example.order_service.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatisRequestUp {
    private String type;
    private Integer month;
    private Integer week;
}
