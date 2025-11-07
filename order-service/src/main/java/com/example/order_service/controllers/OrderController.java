package com.example.order_service.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.order_service.dtos.request.OrderStatisticsRequest;
import com.example.order_service.dtos.response.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.example.order_service.dtos.request.OrderStatisRequestUp;
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
    public OrderDetailResponse createOrder(@RequestBody OrderRequest request) {
        log.debug("REST request to save Order : {}", request.getFullName());
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getOrderById(@PathVariable("id") int id) {
        log.debug("REST request to get Order by id : {}", id);
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{userId}")
    public List<OrderDetailResponse> getOrderByUserId(@PathVariable("userId") int userId) {
        log.debug("REST request to get Order by userId : {}", userId);
        return orderService.getOrderByUserId(userId);
    }

    @GetMapping("/statistics/by-user/{userId}")
    public CntOrderResponse countOrders(@PathVariable("userId") int userId) {
        log.debug("REST request to count Orders userId : {}", userId);
        return orderService.statisOrder(userId);
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable("userId") int userId) {
        log.debug("REST request to delete Orders of User with id : {}", userId);
        orderService.deleteUser(userId);
        return "deleted successfully";
    }

    @DeleteMapping("/order/{id}")
    public Integer deleteOrder(@PathVariable("id") int id) {
        log.debug("REST request to delete Orders id : {}", id);
        return orderService.deleteOrder(id);
    } 

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable("id") int id
                           , @RequestBody OrderRequest request){
        log.info("Change order by id : {}", id);
        return  orderService.updateOrder(id, request);
    }

    @PutMapping("/{id}/status")
    public OrderDetailResponse updateStatus (@PathVariable("id") int id
                                            ,@RequestParam String status){
        log.info("Change order status by id : {}", id);
        // check order
        Order order = orderService.updateStatus(id, status);

        // call list order items
        List<OrderItemResponse> orderItems = new ArrayList<>();
        try {
            orderItems = orderDetailMapper.getOrderItemsByOrderId(id);
        } catch (Exception e) {
            log.error("Error while fetching order items for order id {}: {}", id, e.getMessage());
        }

        return OrderDetailResponse.builder()
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
    }

    @GetMapping("/week/statics")
    public OrderStatisticsResponse getStatisticsWeekly (
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
        return orderService.getWeeklyStatics(request);
    }

    @PostMapping("/update/statics")
    public OrderStatisticsResponse getStatisticUpdate(@RequestBody OrderStatisRequestUp request) {
        log.info("thong ke order theo thang");
        return orderService.getUpdatedStatics(request);
    }

    @PostMapping("/statistics")
    public OrderStatisticsResponse getStatics(@Valid @RequestBody OrderStatisticsRequest request) {
        log.info("thong ke order");

        String type = request.getType() != null ? request.getType().trim().toUpperCase() : "";
        switch (type) {
            case "W" -> { return orderService.getWeek(request); }
            case "M" -> { return orderService.getMonth(request); }
            default -> {
                return
                        OrderStatisticsResponse.builder()
                                .type("INVALID")
                                .totalAmount(BigDecimal.ZERO)
                                .details(Collections.emptyList())
                                .build();
            }
        }
    }

}
