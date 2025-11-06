package com.example.order_service.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(Integer orderId) {
        super("Order not found with id: " + orderId);
    }
}
