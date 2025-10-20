package com.example.user_service.exceptions;

public class UnauthorizedException  extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }
}
