package com.example.orderdetail_service.models;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetail {
    
    private Integer id;

    private Integer orderId;

    private Integer productId;

    private BigDecimal price;

    private Integer numberOfProducts;

    private BigDecimal totalMoney;

    private String color;

}