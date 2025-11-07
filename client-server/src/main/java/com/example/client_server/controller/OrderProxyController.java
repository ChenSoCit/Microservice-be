package com.example.client_server.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.client_server.client.OrderClient;
import com.example.client_server.client.ProductClient;
import com.example.client_server.client.UserClient;
import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.OrderItemDetail;
import com.example.client_server.dto.request.MapOrderRequest;
import com.example.client_server.dto.request.OrderItemRequest;
import com.example.client_server.dto.request.OrderRequest;
import com.example.client_server.dto.request.OrderStatisRequestUp;
import com.example.client_server.dto.request.OrderStatisticsRequest;
import com.example.client_server.dto.response.CntOrderResponse;
import com.example.client_server.dto.response.OrderItemResponse;
import com.example.client_server.dto.response.OrderResponse;
import com.example.client_server.dto.response.OrderStatisticsResponse;
import com.example.client_server.dto.response.OrderStatisticsWeeklyResponse;
import com.example.client_server.dto.response.ProductResponse;
import com.example.client_server.dto.response.ResponseStatistic;
import com.example.client_server.dto.response.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/client/orders")
@RequiredArgsConstructor
@Slf4j(topic = "ORDER-CONTROLLER")
public class OrderProxyController {
    private final OrderClient orderClient;
    private final UserClient userClient;
    private final ProductClient productClient;
    

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<OrderResponse> getOrder(@PathVariable("id") int id){
        OrderResponse orderResponse = orderClient.getOrder(id);
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .data(orderResponse)
                .message("Order fetched successfully")
                .build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<List<OrderResponse>> getOrdersByUserId(HttpServletRequest request, @PathVariable("userId") int userId){
        String username = (String) request.getAttribute("X-Username");
        log.info("Creating order for user: {}", username);

        // Kiem tra user ton tai
        UserResponse userResponse = userClient.findByName(username);
        if (userResponse == null) {
            log.error("User not found: {}", username);
            throw new RuntimeException("User not found");
        }
        // kiem tra quyen truy cap
        if(!request.isUserInRole("ADMIN") && userResponse.getId() != userId){
            log.error("Access denied for user: {} to access userId: {}", username, userId);
            throw new RuntimeException("Access denied");
        }

        log.info("Fetching orders for userId: {} by user: {}", userId, username);

        List<OrderResponse> orders = orderClient.getOrdersByUserId(userId);

        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .data(orders)
                .message("Orders fetched successfully")
                .build();
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<OrderResponse> createOrder(HttpServletRequest request, @RequestBody OrderRequest orderRequest){
        String username = (String) request.getAttribute("X-Username");
        log.info("Creating order for user: {}", username);

        // Kiem tra user ton tai
        UserResponse userResponse = userClient.findByName(username);
        if (userResponse == null) {
            log.error("User not found: {}", username);
            throw new RuntimeException("User not found");
        }
        // Kiem tra product
        List<OrderItemRequest> items = orderRequest.getItems();
        List<OrderItemDetail> orderItemDetails = new ArrayList<>();
        for (OrderItemRequest item : items) {
            // Lay thong tin product tu Product Service
            ProductResponse product = productClient.getProduct(item.getProductId());
            if (product == null) {
                log.error("Product not found: {}", item.getProductId());
                throw new RuntimeException("Product not found");
            }
            if (item.getQuantity() > product.getStockQuantity()) {
                log.error("Invalid quantity: {}", item.getQuantity());
                throw new RuntimeException("Khong du so luong hang");
            }

            // Tao OrderItemDetail
            OrderItemDetail itemDetail = OrderItemDetail.builder()
                    .productId(product.getId())
                    .productName(product.getNameProduct())
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItemDetails.add(itemDetail);
            log.info(" Product validated - ID: {}, Name: {}, Quantity: {}",
                    product.getId(), product.getNameProduct(), item.getQuantity());
        }

        // Tao OrderRequest de gui sang Order Service
        MapOrderRequest mapOrderRequest = MapOrderRequest.builder()
                .userId(userResponse.getId())
                .fullName(orderRequest.getFullName())
                .phone(orderRequest.getPhone())
                .shippingAddress(orderRequest.getShippingAddress())
                .paymentMethod(orderRequest.getPaymentMethod())
                .note(orderRequest.getNote())
                .items(orderItemDetails)
                .build();

        // Goi Order Service de tao order
        OrderResponse orderResponse = orderClient.createOrder(mapOrderRequest);
        if(orderResponse == null){
            log.error("Failed to create order for user: {}", username);
            throw new RuntimeException("Failed to create order");
        }
        log.info("Order created successfully - Order ID: {}, User: {}", orderResponse.getId(), username);

        // update stock
        try {
            List<OrderItemRequest> items1 = orderRequest.getItems();
            for(OrderItemRequest item : items1){
                String result = productClient.updateStockDecrease(item.getProductId(), item.getQuantity());
                log.info(" Stock decreased for product {}: {}", item.getProductId(), result);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Order created successfully")
                .data(orderResponse)
                .build();
    }

    @GetMapping("/statistics/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CntOrderResponse> countStatisticsOrders(HttpServletRequest request, @PathVariable("userId") int userId){
        String username = (String) request.getAttribute("X-Username");
        log.info("Creating order for user: {}", username);

        UserResponse userResponse = userClient.getUser(userId);
        if (userResponse == null) {
            log.error("User not found: {}", username);
            throw new RuntimeException("User not found");
        }

        CntOrderResponse response = orderClient.countOrders(userId);
        response.setUser(userResponse);

        return ApiResponse.<CntOrderResponse>builder()
                .code(200)
                .message("Orders fetched successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/order/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<String> deleteOrder(@PathVariable("id") int id){
        String result = orderClient.deleteOrder(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Order deleted successfully")
                .data(result)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ApiResponse<OrderResponse> updateOrder(@PathVariable("id") int id
                                                    , @RequestBody OrderRequest request){
        return orderClient.updateOrder(id, request);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> changeStatus(@PathVariable("id") int id, @RequestParam(required = false) String status){
        OrderResponse orderResponse = orderClient.updateOrderStatus(id, status);
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Order status changed successfully")
                .data(orderResponse)
                .build();
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable("orderId") int orderId, @RequestParam(required = false) String status){
        // update order
        orderClient.updateOrderStatus(orderId, status);
        // check order
        OrderResponse orderResponse = orderClient.getOrder(orderId);

        List<OrderItemResponse> items1 = orderResponse.getOrderItems();
        for(OrderItemResponse item : items1){
            String result = productClient.updateStockIncrease(item.getProductId(), item.getQuantity());
            log.info("Stock Increased for product {}: {}", item.getProductId(), result);
        }
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Order cancelled successfully")
                .data(orderResponse)
                .build();
    }

    @GetMapping("/week/statics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Object> getOrderStatisticWeek(
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                      @RequestParam(required = false) Integer month,
                                                      @RequestParam(required = false) Integer year){

        OrderStatisticsResponse result = orderClient.getStatisticsWeekly(startDate, endDate, month, year);
        return ApiResponse.builder()
                .code(200)
                .message("get order statistic week")
                .data(result)
                .build();
    }

    @PostMapping("/update/statics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Object> getOrderStatisticUpdate(@RequestBody OrderStatisRequestUp request){
        OrderStatisticsWeeklyResponse result = orderClient.getStatisticsUpdate(request);
        return ApiResponse.builder()
                .code(200)
                .message("get order statistic week")
                .data(result)
                .build();
    }

    @PostMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Object> getOrderStatistics(@RequestBody OrderStatisticsRequest request){

        ResponseStatistic result = orderClient.getStatistics(request);
        return ApiResponse.builder()
                .code(200)
                .message("get order statistics")
                .data(result)
                .build();
    }

}
