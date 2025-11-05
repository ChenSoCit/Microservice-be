package com.example.order_service.services;

import java.math.BigDecimal;
import java.util.List;

import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.request.OrderStatisticsRequest;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.dtos.response.OrderStatisticsResponse;
import com.example.order_service.models.Order;

public interface OrderService {
    OrderDetailResponse createOrder(OrderRequest request);

    OrderDetailResponse getOrderById(int id);

    List<OrderDetailResponse> getOrderByUserId(int userId);

    CntOrderResponse statisOrder(int userId);

    int deleteUser(Integer id);

    int deleteOrder(Integer id);

    String increaseTotalMoney(Integer orderId, BigDecimal totalMoney);

    String decreaseTotalMoney(Integer orderId, BigDecimal totalMoney);

    Order updateOrder(Integer id, OrderRequest req);

    Order updateStatus(Integer id, String status);

    OrderDetailResponse cancelOrder(int orderId, String reason);

    OrderStatisticsResponse getWeeklyStatics(OrderStatisticsRequest request);

    OrderStatisticsResponse getMonthlyStatics(OrderStatisticsRequest request);

    OrderStatisticsResponse getWeek(OrderStatisticsRequest request);
    
    OrderStatisticsResponse getMonth(OrderStatisticsRequest request);
}
