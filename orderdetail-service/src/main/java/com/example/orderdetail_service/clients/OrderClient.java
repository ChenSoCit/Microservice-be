package com.example.orderdetail_service.clients;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.orderdetail_service.dtos.response.ApiResponse;
import com.example.orderdetail_service.dtos.response.OrderResponse;

@FeignClient(name = "order-service", path="/api/v1/orders")
public interface OrderClient {

    @PutMapping("/{orderId}/total-money/increase")
    ApiResponse<String> increaseTotal(@PathVariable("orderId") int orderId,
                       @RequestParam("totalMoney") BigDecimal totalMoney);

    @PutMapping("/{orderId}/total-money/decrease")
    ApiResponse<String> decreaseTotal(@PathVariable("orderId") int orderId,
                       @RequestParam("totalMoney") BigDecimal totalMoney);

    @GetMapping("/{id}")
    ApiResponse<OrderResponse> getOrderById(@PathVariable("id") int id);

    @DeleteMapping("/order/{id}")
    ApiResponse<Integer> deleteOrder(@PathVariable("id") int id);
}
