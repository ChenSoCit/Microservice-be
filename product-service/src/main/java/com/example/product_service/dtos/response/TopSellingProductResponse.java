package com.example.product_service.dtos.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopSellingProductResponse {
    private Integer productId;
    private String nameProduct;
    private Integer categoryId;
    private BigDecimal price;
    private Long totalSold;
}
