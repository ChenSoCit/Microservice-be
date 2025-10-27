package com.example.order_service.mappers;

import java.util.List;

import com.example.order_service.dtos.response.OrderItemResponse;
import com.example.order_service.models.OrderDetail;

public interface OrderDetailMapper {
    int createOrderDetail(OrderDetail orderDetail);
    int changeQuantity(int id, int quantity);
    void deleteOrderDetail(int id);
    OrderItemResponse getOrderDetailById(int id);
    List<OrderItemResponse> getOrderItemsByOrderId(int orderId);
}
