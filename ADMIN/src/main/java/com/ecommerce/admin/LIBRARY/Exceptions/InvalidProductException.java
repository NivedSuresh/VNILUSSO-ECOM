package com.ecommerce.admin.LIBRARY.Exceptions;

import lombok.Getter;

@Getter
public class InvalidProductException extends RuntimeException{
    String viewMessage;
    public InvalidProductException(String message, String viewMessage) {
        this.viewMessage = viewMessage;
    }
}
