package com.shopapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) 
public class ExpiredTokenException extends Exception{

    public ExpiredTokenException(String message) {
        super(message);
    }
    
}
