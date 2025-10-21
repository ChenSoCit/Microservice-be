package com.example.client_server.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.client_server.client.OrderClient;
import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.CntOrderResponse;
import com.example.client_server.dto.OrderDetailResponse;
import com.example.client_server.dto.OrderRequest;
import com.example.client_server.dto.OrderResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderProxyController {
    private final OrderClient orderClient;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<OrderDetailResponse> getOrder(@PathVariable("id") long id){
        return orderClient.getOrder(id);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<List<OrderResponse>> getOrdersByUserId(@PathVariable("userId") long userId){
        return orderClient.getOrdersByUserId(userId);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> createOrder(@RequestBody OrderRequest request){
        return orderClient.createOrder(request);
    }

    @GetMapping("/statistics/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CntOrderResponse> countOrders(@PathVariable("userId") int userId){
        return orderClient.countOrders(userId);
    }

    @DeleteMapping("/order/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ApiResponse<Integer> deleteOrder(@PathVariable("id") int id){
        return orderClient.deleteOrder(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<OrderResponse> updateOrder(@PathVariable("id") int id
                                                    , @RequestBody OrderRequest request){
        return orderClient.updateOrder(id, request);
    }
}
