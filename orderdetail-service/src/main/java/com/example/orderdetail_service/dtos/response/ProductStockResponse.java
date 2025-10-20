package com.example.orderdetail_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductStockResponse {
    private Integer ProductId;
    private Integer StockQuantity;
}
