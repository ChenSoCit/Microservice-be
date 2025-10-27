package com.example.order_service.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order_service.clients.UserClient;
import com.example.order_service.commons.OrderStatus;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.dtos.response.OrderItemResponse;
import com.example.order_service.dtos.response.UserResponse;
import com.example.order_service.mappers.OrderDetailMapper;
import com.example.order_service.mappers.OrderMapper;
import com.example.order_service.models.Order;
import com.example.order_service.models.OrderDetail;
import com.example.order_service.services.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderDetailResponse createOrder(OrderRequest request) {
        log.info("Request to create Order: {}", request);
        
        // Validate request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.error("Order items cannot be empty");
            throw new IllegalArgumentException("Order items cannot be empty");
        }
        
        
        // Tính tổng tiền từ các items
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Tạo Order
        Order order = Order.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .orderDate(LocalDateTime.now())
                .note(request.getNote())
                .build();
        
        int orderCreated = orderMapper.createOrder(order);
        if (orderCreated <= 0) {
            log.error("Failed to create order");
            throw new RuntimeException("Failed to create order");
        }
        
        log.info("Order created with id: {}", order.getId());
        
        // Tạo Order Details cho từng sản phẩm
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (var item : request.getItems()) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderId(order.getId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subtotal(subtotal)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            int detailCreated = orderDetailMapper.createOrderDetail(orderDetail);
            if (detailCreated <= 0) {
                log.error("Failed to create order detail for product {}", item.getProductId());
                throw new RuntimeException("Failed to create order detail");
            }


            
            log.info("Order detail created with id: {}", orderDetail.getId());
            
            // Tạo response cho item
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(Math.toIntExact(orderDetail.getId()));
            itemResponse.setOrderId(Math.toIntExact(orderDetail.getOrderId()));
            itemResponse.setProductId(item.getProductId());
            itemResponse.setPrice(item.getPrice());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setSubTotal(subtotal);
            
            orderItemResponses.add(itemResponse);
        }
        
        // Tạo response
        OrderDetailResponse response = OrderDetailResponse.builder()
                .id(Math.toIntExact(order.getId()))
                .userId(Math.toIntExact(order.getUserId()))
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .orderItems(orderItemResponses)
                .build();
        
        log.info("Order created successfully with {} items, total: {}", 
                orderItemResponses.size(), totalAmount);
        
        return response;
    }

    @Override
    @Transactional
    public OrderDetailResponse getOrderById(int id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            log.error("Order with id {} not found", id);
            throw new IllegalArgumentException("Order not found");
        }

        // Lấy danh sách order items
        List<OrderItemResponse> orderItems = orderDetailMapper.getOrderItemsByOrderId(id);

        return OrderDetailResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .orderItems(orderItems)
                .build();
    }

    @Override
    @Transactional
    public List<OrderDetailResponse> getOrderByUserId(int userId) {
        List<Order> orders = orderMapper.getAllOrderByUser(userId);
        if (orders == null || orders.isEmpty()) {
            log.error("No orders found for user id {}", userId);
            throw new IllegalArgumentException("No orders found");
        }

        List<OrderDetailResponse> orderResponses = orders.stream().map(order -> {
            // Lấy danh sách order items cho mỗi order
            List<OrderItemResponse> orderItems = orderDetailMapper.getOrderItemsByOrderId(order.getId());

            return OrderDetailResponse.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .fullName(order.getFullName())
                    .phone(order.getPhone())
                    .shippingAddress(order.getShippingAddress())
                    .paymentMethod(order.getPaymentMethod())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .totalAmount(order.getTotalAmount())
                    .note(order.getNote())
                    .orderItems(orderItems)
                    .build();
        })
        .collect(Collectors.toList());
        
        return orderResponses;
    }

    @Override
    @Transactional
    public CntOrderResponse statisOrder(int userId) {
        CntOrderResponse response = orderMapper.statisOrder(userId);

        // Nếu user chưa có order nào
        if (response == null || response.getTotalOrder() == null) {
        log.info("User {} has no orders yet", userId);
        return CntOrderResponse.builder()
                .totalOrder(0)
                .statusPending(0)
                .statusDelivering(0)
                .statusCompleted(0)
                .totalMoney(BigDecimal.ZERO)
                .build();
            }

        return response;
    }

    @Override
    public int deleteUser(Integer id) {
        log.info("Request to delete Orders of User with id: {}", id);
        Order order = orderMapper.getOrderById(id);

        if(order != null && !"PENDING".equals(order.getStatus().toString())){
            log.warn("Order has been sent, cannot cancel");
            throw new IllegalStateException("Order has been sent, cannot cancel");
        }

        // Xóa orders và order details (cascade)
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
    public Order updateOrder(Integer id, OrderRequest req) {
        Order existingOrder = orderMapper.getOrderById(id);
        if(existingOrder == null){
            log.error("Order not found with id: {}", id);
            throw new IllegalArgumentException("Order not found");
        }

        // Tính lại tổng tiền từ items
        BigDecimal totalAmount = req.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order update = Order.builder()
            .id((id))
            .userId(req.getUserId())
            .fullName(req.getFullName())
            .shippingAddress(req.getShippingAddress())
            .phone(req.getPhone())
            .orderDate(existingOrder.getOrderDate())
            .status(existingOrder.getStatus())
            .totalAmount(totalAmount)
            .paymentMethod(req.getPaymentMethod())
            .note(req.getNote())
        .build();

        int rows = orderMapper.updateOrder(update);
        log.info("Updated {} rows", rows);

        // Trả về order đã cập nhật
        return orderMapper.getOrderById(id);
    }

    @Override
    @Transactional
    public Order updateStatus(Integer id, String status) {
        Order order = orderMapper.getOrderById(id);
        if(order == null){
            log.error("Order not found with id: {}", id);
            throw new IllegalArgumentException("Order not found");
        }

        int rows = orderMapper.updateStatus(id, status);
        log.info("rows affected = {}", rows);

        // Lấy lại order đã cập nhật
        return orderMapper.getOrderById(id);
    }
}
