package com.example.order_service.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.order_service.commons.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class OrderDetailResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone_number")
    private String phone;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("note")
    private String note;

    @JsonProperty("orderItems")
    private List<OrderItemResponse> orderItems;
}
