package com.example.orderdetail_service.services;

import java.util.List;

import com.example.orderdetail_service.dtos.request.OrderDetailRequest;
import com.example.orderdetail_service.dtos.response.OrderDetailResponse;
import com.example.orderdetail_service.dtos.response.TopProductStatResponse;

public interface OrderDetailService {
    int createOrderDetail(OrderDetailRequest request);

    OrderDetailResponse getDetailById(Integer id);

    OrderDetailResponse updateDetailById(Integer id, OrderDetailRequest request);

    int deleteDetailById(Integer id);

    List<TopProductStatResponse> getTopProduct();

    void deleteAllOrderDetail(Integer orderId);
}
