package com.example.product_service.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductStockRequest {
    private Integer productId;
    private Integer quantity;
}
