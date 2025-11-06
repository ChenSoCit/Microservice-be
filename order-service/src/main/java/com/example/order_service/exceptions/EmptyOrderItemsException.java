package com.example.order_service.exceptions;

public class EmptyOrderItemsException extends RuntimeException {
    public EmptyOrderItemsException(String message) {
        super(message);
    }
    
    public EmptyOrderItemsException() {
        super("Order items cannot be empty");
    }
}
