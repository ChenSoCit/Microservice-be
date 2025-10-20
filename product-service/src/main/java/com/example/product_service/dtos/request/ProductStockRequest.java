package com.example.product_service.dtos.request;

import lombok.Data;

@Data
public class ProductStockRequest {
    private Integer productId;
    private Integer quantity;
}
