package com.example.client_server.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.CntOrderResponse;
import com.example.client_server.dto.OrderDetailResponse;
import com.example.client_server.dto.OrderRequest;
import com.example.client_server.dto.OrderResponse;
import com.example.client_server.dto.OrderStatus;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderClient {

    @GetMapping("/{id}")
    ApiResponse<OrderDetailResponse> getOrder(@PathVariable("id") long id);

    @GetMapping("/user/{userId}")
    ApiResponse<List<OrderResponse>> getOrdersByUserId(@PathVariable("userId") long userId);

    @PostMapping("")
    ApiResponse<String> createOrder(@RequestBody OrderRequest request);

    @PutMapping("/{id}/status")
    ApiResponse<OrderResponse> updateOrderStatus(@PathVariable("id") long id, @RequestParam OrderStatus status);

    @GetMapping("/statistics/by-user/{userId}")
    ApiResponse<CntOrderResponse> countOrders(@PathVariable("userId") int userId);

    @DeleteMapping("/order/{id}")
    ApiResponse<Integer> deleteOrder(@PathVariable("id") int id);

    @PutMapping("/{id}")
    ApiResponse<OrderResponse> updateOrder(@PathVariable("id") int id
                                                    , @RequestBody OrderRequest request);
}
