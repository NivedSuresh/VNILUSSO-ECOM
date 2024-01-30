package com.ecommerce.admin.LIBRARY.Exceptions;

import lombok.Getter;

@Getter
public class CustomerBlockedException extends RuntimeException{

    String viewMessage;
    public CustomerBlockedException(String message, String viewMessage) {
        super(message);
        this.viewMessage = viewMessage;
    }
}
