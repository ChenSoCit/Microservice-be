package com.example.user_service.exceptions;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message){
        super(message);
    }

}
