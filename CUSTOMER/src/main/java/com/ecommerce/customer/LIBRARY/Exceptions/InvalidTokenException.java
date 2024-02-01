package com.ecommerce.customer.LIBRARY.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@Getter
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Invalid Token or is Expired")
public class InvalidTokenException extends RuntimeException{
    String viewMessage;
    public InvalidTokenException(String message, String viewMessage){
        super(message);
        this.viewMessage = viewMessage;
    }
}