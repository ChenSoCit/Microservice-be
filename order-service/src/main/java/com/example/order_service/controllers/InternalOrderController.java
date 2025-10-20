package com.example.order_service.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.commons.OrderStatus;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.OrderResponse;
import com.example.order_service.services.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/internal/orders")
@Slf4j(topic="INTERNAL-CONTROLLER")
public class InternalOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/statistics/by-user/{userId}")
    public ResponseEntity<CntOrderResponse> countOrders(@PathVariable("userId") int userId) {
        log.debug("REST request to count Orders");
        CntOrderResponse result = orderService.statisOrder(userId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{orderId}/total-money/increase")
    public ResponseEntity<String> increaseMoney(@PathVariable("orderId") int orderId,
                                                @RequestParam BigDecimal totalMoney){
        orderService.increaseTotalMoney(orderId, totalMoney);
        return ResponseEntity.ok("update increase total money");
    }

    @PutMapping("/{orderId}/total-money/decrease")
    public ResponseEntity<String> decreaseMoney(@PathVariable("orderId") int orderId,
                                                @RequestParam BigDecimal totalMoney){
        orderService.decreaseTotalMoney(orderId, totalMoney);
        return ResponseEntity.ok("update decrease total money");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus (@PathVariable("id") int id
                                                        ,@RequestParam OrderStatus status){
        OrderResponse order = orderService.updateStatus(id, status);

        return ResponseEntity.ok(order);
    }

}
