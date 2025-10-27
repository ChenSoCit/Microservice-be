package com.example.order_service.controllers;

import java.util.List;

import com.example.order_service.commons.OrderStatus;
import com.example.order_service.mappers.OrderDetailMapper;
import com.example.order_service.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.response.ApiResponse;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderItemResponse;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.services.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "ORDER-CONTROLLER")
@RequestMapping("/api/v1/orders")
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

}
