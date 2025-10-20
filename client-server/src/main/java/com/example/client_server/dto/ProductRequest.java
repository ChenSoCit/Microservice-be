package com.example.client_server.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ProductRequest {
    private String nameProduct;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private Integer categoryId;

}
