package com.ecommerce.admin.LIBRARY.Exceptions;

public class RefundFailedException extends RuntimeException{
    public RefundFailedException(String message) {
        super(message);
    }
}
