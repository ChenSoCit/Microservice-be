package com.example.orderdetail_service.dtos.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailResponse {

    private Integer id;

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("product_id")
    private Integer productId;

    private Double price;

    @JsonProperty("number_of_products")
    private Integer numberOfProducts;

    @JsonProperty("total_money")
    private BigDecimal totalMoney;

    private String color;
}
