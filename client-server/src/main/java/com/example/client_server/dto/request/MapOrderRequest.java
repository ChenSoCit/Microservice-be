package com.example.client_server.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.example.client_server.dto.OrderItemDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapOrderRequest {
    private Integer userId;

    private String fullName;

    private String phone;

    private String shippingAddress;

    private String paymentMethod;

    private String note;

    private List<OrderItemDetail> items;
}
