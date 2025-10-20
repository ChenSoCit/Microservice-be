package com.example.order_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.response.ApiResponse;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.dtos.response.OrderResponse;
import com.example.order_service.services.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "ORDER-CONTROLLER")
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<String>> createOrder(@RequestBody OrderRequest request) {
        log.debug("REST request to save Order : {}", request);
        String rows = orderService.createOrder(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
            .code(HttpStatus.OK.value())
            .message("create order")
            .data(rows)
            .build();
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderById(@PathVariable("id") int id) {
        log.debug("REST request to get Order by id : {}", id);

        ApiResponse<OrderDetailResponse> response = ApiResponse.<OrderDetailResponse>builder()
            .code(HttpStatus.OK.value())
            .message("Get order detail by id")
            .data(orderService.getOrderById(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderByUserId(@PathVariable("userId") int userId) {
        log.debug("REST request to get Order by userId : {}", userId);
        List<OrderResponse> getOrder = orderService.getOrderByUserId(userId);

        ApiResponse<List<OrderResponse>> response = ApiResponse.<List<OrderResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("get order by user id")
            .data(getOrder)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/by-user/{userId}")
    public ResponseEntity<ApiResponse<CntOrderResponse>> countOrders(@PathVariable("userId") int userId) {
        log.debug("REST request to count Orders");
        CntOrderResponse result = orderService.statisOrder(userId);

        ApiResponse<CntOrderResponse> response = ApiResponse.<CntOrderResponse>builder()
            .code(HttpStatus.OK.value())
            .message("thong ke so luong don hang")
            .data(result)
            .build();
        return ResponseEntity.ok(response);
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
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(@PathVariable("id") int id
                                                    , @RequestBody OrderRequest request){
        OrderResponse order = orderService.updateOrder(id, request);    
        
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
            .code(HttpStatus.OK.value())
            .message("update order by id")
            .data(order)
            .build();

        return ResponseEntity.ok(response);

    }

}
