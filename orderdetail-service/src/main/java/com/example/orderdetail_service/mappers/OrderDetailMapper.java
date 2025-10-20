package com.example.orderdetail_service.mappers;

import java.util.List;

import com.example.orderdetail_service.dtos.response.OrderDetailResponse;
import com.example.orderdetail_service.dtos.response.TopProductStatResponse;
import com.example.orderdetail_service.models.OrderDetail;

public interface OrderDetailMapper {
    
    int insert(OrderDetail row);

    OrderDetailResponse getById(Integer id);

    int updateById(OrderDetail row);

    int deleteById(Integer id);

    int deleteByOrder(Integer orderid);
    
    int insertSelective(OrderDetail row);

    List<TopProductStatResponse> getTopProduct();

    List<OrderDetailResponse> findDetailResponses(Integer orderId);
}