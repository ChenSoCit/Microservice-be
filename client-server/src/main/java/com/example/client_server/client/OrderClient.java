package com.example.client_server.client;

import java.time.LocalDate;
import java.util.List;

import com.example.client_server.dto.request.OrderStatisticsRequest;
import com.example.client_server.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.request.MapOrderRequest;
import com.example.client_server.dto.request.OrderRequest;
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

    @GetMapping("/week/statics")
    OrderStatisticsResponse getStatisticsWeekly(
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
                                                @RequestParam(required = false) Integer month,
                                                @RequestParam(required = false) Integer year);

    @GetMapping("/update/statics")
    OrderStatisticsWeeklyResponse getStatisticsUpdate(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week);

    @PostMapping("/statistics")
    ResponseStatistic getStatistics(@RequestBody OrderStatisticsRequest request);

    @DeleteMapping("/order/{id}")
    String deleteOrder(@PathVariable("id") int id);

    @PutMapping("/{id}")
    ApiResponse<OrderResponse> updateOrder(@PathVariable("id") int id
                                        , @RequestBody OrderRequest request);

    @PostMapping("/{orderId}/cancel")
    OrderResponse cancelOrder(@PathVariable("orderId") int orderId, @RequestParam String status);
}
