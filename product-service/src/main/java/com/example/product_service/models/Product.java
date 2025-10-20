package com.example.product_service.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private Integer id;

    private String nameProduct;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer categoryId;

}