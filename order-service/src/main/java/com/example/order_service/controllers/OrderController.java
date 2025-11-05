package com.example.order_service.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.example.order_service.dtos.request.OrderStatisticsRequest;
import com.example.order_service.dtos.response.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.mappers.OrderDetailMapper;
import com.example.order_service.models.Order;
import com.example.order_service.services.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "ORDER-CONTROLLER")
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;


    @PostMapping("")
    public ResponseEntity<OrderDetailResponse> createOrder(@RequestBody OrderRequest request) {
        log.debug("REST request to save Order : {}", request);
        OrderDetailResponse orderResponse = orderService.createOrder(request);

        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderById(@PathVariable("id") int id) {
        log.debug("REST request to get Order by id : {}", id);
        OrderDetailResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderByUserId(@PathVariable("userId") int userId) {
        log.debug("REST request to get Order by userId : {}", userId);
        List<OrderDetailResponse> getOrder = orderService.getOrderByUserId(userId);
        return ResponseEntity.ok(getOrder);
    }

    @GetMapping("/statistics/by-user/{userId}")
    public ResponseEntity<CntOrderResponse> countOrders(@PathVariable("userId") int userId) {
        log.debug("REST request to count Orders");
        CntOrderResponse result = orderService.statisOrder(userId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Integer>> deleteUser(@PathVariable("userId") int userId) {
        log.debug("REST request to delete Orders of User with id : {}", userId);
        int result = orderService.deleteUser(userId);
        
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
            .code(HttpStatus.OK.value())
            .message("delete user by id")
            .data(result)
            .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<ApiResponse<Integer>> deleteOrder(@PathVariable("id") int id) {
        log.debug("REST request to delete Orders of User with id : {}", id);
        int result = orderService.deleteOrder(id);
        
        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
            .code(HttpStatus.OK.value())
            .message("delete user by id")
            .data(result)
            .build();
        return ResponseEntity.ok(response);
    } 

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") int id
                                                    , @RequestBody OrderRequest request){
        Order order = orderService.updateOrder(id, request);

        return ResponseEntity.ok(order);

    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailResponse> updateStatus (@PathVariable("id") int id
                                                        ,@RequestParam String status){
        // check order
        Order order = orderService.updateStatus(id, status);

        // call list order items
        List<OrderItemResponse> orderItems = orderDetailMapper.getOrderItemsByOrderId(id);

        OrderDetailResponse response = OrderDetailResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .note(order.getNote())
                .totalAmount(order.getTotalAmount())
                .orderItems(orderItems)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/week/statics")
    public ResponseEntity<OrderStatisticsResponse> getStatisticsWeekly (
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year){
        OrderStatisticsRequest request = OrderStatisticsRequest.builder()
                .month(month)
                .year(year)
                .build();
        if(startDate != null && endDate != null){
            request.setStartDate(startDate);
            request.setEndDate(endDate);
        }
        return ResponseEntity.ok(orderService.getWeeklyStatics(request));
    }

    @GetMapping("/month/statics")
    public ResponseEntity<OrderStatisticsResponse> getStatisticMonthly(@Valid @RequestBody OrderStatisticsRequest request){
        return ResponseEntity.ok(orderService.getMonthlyStatics(request));
    }

    @PostMapping("/statistics")
    public ResponseEntity<OrderStatisticsResponse> getStatics(@Valid @RequestBody OrderStatisticsRequest request) {

        String type = request.getType() != null ? request.getType().trim().toUpperCase() : "";
        switch (type) {
            case "W" -> { return ResponseEntity.ok(orderService.getWeek(request)); }
            case "M" -> { return ResponseEntity.ok(orderService.getMonth(request)); }
            default -> {
                return ResponseEntity.badRequest()
                        .body(OrderStatisticsResponse.builder()
                                .type("INVALID")
                                .totalAmount(BigDecimal.ZERO)
                                .details(Collections.emptyList())
                                .build());
            }
        }
    }

}
