package com.example.order_service.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order_service.clients.UserClient;
import com.example.order_service.commons.OrderStatus;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.dtos.response.OrderResponse;
import com.example.order_service.dtos.response.UserResponse;
import com.example.order_service.mappers.OrderMapper;
import com.example.order_service.models.Order;
import com.example.order_service.services.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    public String createOrder(OrderRequest request) {
        log.info("Request to create Order: {}", request);

        UserResponse existingUser = userClient.getUserById(request.getUserId());
       
        if (existingUser == null){
            log.error("User with id {} not found", request.getUserId());
            throw new IllegalArgumentException("User not found");
        }
        
        Order order = Order.builder()
            .userId(request.getUserId())
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .address(request.getAddress())
            .email(request.getEmail())
            .status("PENDING")
            .totalMoney(BigDecimal.ZERO)
            .orderDate(LocalDateTime.now())
            .build();
        orderMapper.createOrder(order);
    return "Order created successfully"; 
    }

    @Override
    @Transactional
    public OrderDetailResponse getOrderById(int id) {
        Order order = orderMapper.getOrderById(id);
        if (order != null) {
            UserResponse existingUser = userClient.getUserById(order.getId());

            OrderDetailResponse response = new OrderDetailResponse();
            response.setId(order.getId());
            response.setOrderDate(order.getOrderDate());
            response.setStatus(order.getStatus());
            response.setTotalMoney(order.getTotalMoney());
            response.setUser(existingUser);
            return response;
        }

        throw new IllegalArgumentException("Order not found");
    }

    @Override
    @Transactional
    public List<OrderResponse> getOrderByUserId(int userId) {
        List<OrderResponse> responses = orderMapper.getOrderByUserId(userId);
        return responses;
    }

    @Override
    @Transactional
    public CntOrderResponse statisOrder(int userId) {
        CntOrderResponse response = orderMapper.statisOrder(userId);

        UserResponse existingUser = userClient.getUserById(userId);

        if (existingUser == null) {
            log.error("User with id {} not found", userId);
            throw new IllegalArgumentException("User not found");
        }
        CntOrderResponse response1 = new CntOrderResponse();
        response1.setUser(existingUser);
        response1.setTotalOrder(response.getTotalOrder());
        response1.setStatusPending(response.getStatusPending());
        response1.setStatusDelivering(response.getStatusDelivering());
        response1.setStatusCompleted(response.getStatusCompleted());
        response1.setTotalMoney(response.getTotalMoney());

        return response1;
    }

    @Override
    public int deleteUser(Integer id) {
        log.info("Request to delete Orders of User with id: {}", id);
        Order order = orderMapper.getOrderById(id);

        if(!"PENDING".equals(order.getStatus())){
            log.info("Don hang da duoc gui khong the huy");
        }

        // hoan tra hang
        int rows = orderMapper.deleteUser(id);
        log.info("Deleted {} orders of User with id: {}", rows, id);
        return rows;
    }

    @Override
    public int deleteOrder(Integer id){
        return orderMapper.deleteOrder(id);
    }

    @Override
    public String increaseTotalMoney(Integer orderId, BigDecimal totalMoney) {
        orderMapper.increaseTotalMoney(orderId, totalMoney);
        return "Success";
    }

    @Override
    public String decreaseTotalMoney(Integer orderId, BigDecimal totalMoney) {
       orderMapper.decreaseTotalMoney(orderId, totalMoney);
       return "Success";
    }

    @Override
    public OrderResponse updateOrder(Integer id, OrderRequest req) {
        Order order = orderMapper.getOrderById(id);
        if(order == null){
            log.info("Order not found");
        }

        Order update = Order.builder()
            .id(id)
            .userId(req.getUserId())
            .fullName(req.getFullName())
            .address(req.getAddress())
            .email(req.getEmail())
            .phone(req.getPhone())
            .orderDate(order.getOrderDate())
            .status(order.getStatus())
            .totalMoney(order.getTotalMoney())
        .build();

        int rows = orderMapper.updateOrder(update);
        log.info("row {}", rows);

        return OrderResponse.builder()
            .id(update.getId())
            .userId(update.getUserId())
            .fullName(update.getFullName())
            .email(update.getEmail())
            .phone(update.getPhone())
            .address(update.getAddress())
            .orderDate(update.getOrderDate())
            .status(OrderStatus.PENDING)
            .totalMoney(update.getTotalMoney())
        .build();
    }

    @Override
    public OrderResponse updateStatus(Integer id, OrderStatus status) {
        Order order = orderMapper.getOrderById(id);
        if(order == null){
            log.info("Order not found");
        }

        int rows = orderMapper.updateStatus(id, status.name());
        log.info("rows affected = {}", rows);

        return OrderResponse.builder()
            .id(id)
            .userId(order.getUserId())
            .fullName(order.getFullName())
            .email(order.getEmail())
            .phone(order.getPhone())
            .address(order.getAddress())
            .orderDate(order.getOrderDate())
            .status(status)  
            .totalMoney(order.getTotalMoney())
            .build();
    }
}
