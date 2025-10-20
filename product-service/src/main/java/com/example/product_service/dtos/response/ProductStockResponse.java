package com.example.product_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductStockResponse {
    private Integer ProductId;
    private Integer StockQuantity;
}
