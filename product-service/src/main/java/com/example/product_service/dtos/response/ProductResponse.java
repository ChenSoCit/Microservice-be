package com.example.product_service.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ProductResponse {
   
    private Integer id;

    private String nameProduct;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer categoryId;
    
}
