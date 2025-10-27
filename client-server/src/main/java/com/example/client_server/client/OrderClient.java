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
import com.example.client_server.dto.response.CntOrderResponse;
import com.example.client_server.dto.request.MapOrderRequest;
import com.example.client_server.dto.response.OrderDetailResponse;
import com.example.client_server.dto.request.OrderRequest;
import com.example.client_server.dto.response.OrderResponse;
import com.example.client_server.dto.OrderStatus;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderClient {

    @GetMapping("/{id}")
    OrderResponse getOrder(@PathVariable("id") int id);

    @GetMapping("/user/{userId}")
    List<OrderResponse> getOrdersByUserId(@PathVariable("userId") int userId);

    @PostMapping("")
    OrderResponse createOrder(@RequestBody MapOrderRequest request);

    @PutMapping("/{id}/status")
    OrderResponse updateOrderStatus(@PathVariable("id") long id, @RequestParam String status);

    @GetMapping("/statistics/by-user/{userId}")
    CntOrderResponse countOrders(@PathVariable("userId") int userId);

    @DeleteMapping("/order/{id}")
    String deleteOrder(@PathVariable("id") int id);

    @PutMapping("/{id}")
    ApiResponse<OrderResponse> updateOrder(@PathVariable("id") int id
                                                    , @RequestBody OrderRequest request);
}
