package com.example.order_service.dtos.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class OrderRequest {
    private int userId;

    private String fullName;

    private String phone;

    private String shippingAddress;

    private String paymentMethod;

    private String note;

    private BigDecimal totalAmount;

    private List<OrderItemDetail> items; 
}
