package com.ecommerce.admin.LIBRARY.Exceptions;

public class InvalidLoginMethodException extends RuntimeException{
    public InvalidLoginMethodException(String message) {
        super(message);
    }
}
