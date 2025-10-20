package com.example.user_service.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.user_service.dtos.response.OrderResponse;

@FeignClient(name = "order-service", path = "/api/internal/orders")
public interface OrderClient {

    @DeleteMapping("/user/{userId}")
    int deleteOrder(int userId);

    @GetMapping("/user/{userId}")
    List<OrderResponse> getOrderByUserId(@PathVariable("userId") int userId);
}
