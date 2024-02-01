package com.ecommerce.customer.LIBRARY.Exceptions;

import lombok.Getter;

@Getter
public class EmailNullException extends RuntimeException{
    private String viewMessage;
    public EmailNullException(String message, String viewMessage) {
        super(message);
        this.viewMessage = viewMessage;
    }
}
