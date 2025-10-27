package com.example.client_server.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("subtotal")
    private BigDecimal subTotal;
}
