package com.example.orderdetail_service.controllers;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderdetail_service.dtos.request.OrderDetailRequest;
import com.example.orderdetail_service.dtos.response.ApiResponse;
import com.example.orderdetail_service.dtos.response.OrderDetailResponse;
import com.example.orderdetail_service.dtos.response.TopProductStatResponse;
import com.example.orderdetail_service.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/order-detail")
@Slf4j(topic = "ORDER-DETAIL-CONTROLLER")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDetailRequest req){
        int rows = orderDetailService.createOrderDetail(req);
        return ResponseEntity.status(Response.SC_ACCEPTED).body(rows);
    }

    @GetMapping("/statistics/top-products")
     public ResponseEntity<ApiResponse<List<TopProductStatResponse>>> getTopProduct(){
        List<TopProductStatResponse> result = orderDetailService.getTopProduct();

        ApiResponse<List<TopProductStatResponse>> response = ApiResponse.<List<TopProductStatResponse>>builder()
            .code(HttpStatus.OK.value())
            .message("Get top product")
            .data(result)
            .build();

        return ResponseEntity.ok(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateNumber(@PathVariable("id") int id, @RequestBody OrderDetailRequest req){
        OrderDetailResponse result = orderDetailService.updateDetailById(id, req);

        ApiResponse<OrderDetailResponse> response = ApiResponse.<OrderDetailResponse>builder()
            .code(HttpStatus.OK.value())
            .message(" Update Order detail by id")
            .data(result)
            .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
     public ResponseEntity<ApiResponse<Integer>> deleteDetail(@PathVariable("id") int id){
        int result = orderDetailService.deleteDetailById(id);

        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
            .code(HttpStatus.OK.value())
            .message("Get top product")
            .data(result)
            .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Integer>> deleteAllDetail(@PathVariable("orderId") int orderId){

        orderDetailService.deleteAllOrderDetail(orderId);

        ApiResponse<Integer> response = ApiResponse.<Integer>builder()
            .code(HttpStatus.OK.value())
            .message("Get top product")
            .data(1)
            .build();

        return ResponseEntity.ok(response);
    }
}
