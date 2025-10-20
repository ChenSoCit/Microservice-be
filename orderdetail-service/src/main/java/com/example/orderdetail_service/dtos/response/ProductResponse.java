package com.example.orderdetail_service.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ProductResponse {
    @JsonProperty("product_id")
    private Integer id;

    @JsonProperty("name_product")
    private String nameProduct;

    private String description;

    private BigDecimal price;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("category_id")
    private Integer categoryId;
    
}
