package com.example.client_server.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatisRequestUp {
    private String type;
    private Integer month;
    private Integer week;
}
