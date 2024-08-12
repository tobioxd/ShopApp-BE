package com.shopapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DataExistAlreadyException extends RuntimeException {
    public DataExistAlreadyException(String message) {
        super(message);
    }
}
