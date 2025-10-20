package com.example.order_service.services;

import java.math.BigDecimal;
import java.util.List;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.commons.OrderStatus;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderResponse;

public interface OrderService {
    String createOrder(OrderRequest request);

    OrderDetailResponse getOrderById(int id);

    List<OrderResponse> getOrderByUserId(int userId);

    CntOrderResponse statisOrder(int userId);

    int deleteUser(Integer id);

    int deleteOrder(Integer id);

    String increaseTotalMoney(Integer orderId, BigDecimal totalMoney);

    String decreaseTotalMoney(Integer orderId, BigDecimal totalMoney);

    OrderResponse updateOrder(Integer id, OrderRequest req);

    OrderResponse updateStatus(Integer id, OrderStatus status);
}
