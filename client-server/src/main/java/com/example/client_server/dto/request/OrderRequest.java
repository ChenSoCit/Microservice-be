package com.example.client_server.dto.request;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class OrderRequest {

    private String fullName;

    private String phone;

    private String shippingAddress;

    private String paymentMethod;

    private String note;

    private List<OrderItemRequest> items;
}
