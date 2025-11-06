package com.example.client_server.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDetail {
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subTotal;
}
