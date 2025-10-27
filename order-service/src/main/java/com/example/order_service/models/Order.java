package com.example.order_service.models;


import com.example.order_service.commons.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int id;
    private int userId;
    private String fullName;
    private OrderStatus status;
    private String phone;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String paymentMethod;
    private String shippingAddress;
    private String note;

    private List<OrderDetail> orderDetails;
}
