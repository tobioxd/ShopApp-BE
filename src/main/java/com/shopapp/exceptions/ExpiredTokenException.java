package com.shopapp.exceptions;

public class ExpiredTokenException extends Exception{

    public ExpiredTokenException(String message) {
        super(message);
    }
    
}
