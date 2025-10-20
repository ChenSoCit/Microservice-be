package com.example.orderdetail_service.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailRequest {

    private Integer orderId;

    private Integer productId;

    private Integer numberOfProducts;

    private String color;
}
