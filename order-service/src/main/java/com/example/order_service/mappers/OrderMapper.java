package com.example.order_service.mappers;

import java.math.BigDecimal;
import java.util.List;

import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderResponse;
import com.example.order_service.models.Order;

import feign.Param;

public interface OrderMapper {
    int createOrder(Order order);

    Order getOrderById(int id);

    List<OrderResponse> getOrderByUserId(int userId);

    CntOrderResponse statisOrder(int userId);

    int deleteUser(Integer id);

    int deleteOrder(Integer id);

    int updateOrder(Order order);

    int decreaseTotalMoney(@Param("orderId") Integer orderId, @Param("totalMoney") BigDecimal totalMoney);

    int increaseTotalMoney(@Param("orderId") Integer orderId, @Param("totalMoney") BigDecimal totalMoney);
        
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
}
