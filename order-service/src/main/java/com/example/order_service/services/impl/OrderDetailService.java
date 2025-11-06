package com.example.order_service.services.impl;

import com.example.order_service.mappers.OrderDetailMapper;
import com.example.order_service.models.OrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ORDER-DETAIL-SERVICE")
@Transactional
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailMapper orderDetailMapper;

    public int createOrderDetail(OrderDetail orderDetail){
        return orderDetailMapper.createOrderDetail(orderDetail);
    }
}
