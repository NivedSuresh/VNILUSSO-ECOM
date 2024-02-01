package com.ecommerce.customer.LIBRARY.Exceptions;

public class RefundFailedException extends RuntimeException{
    public RefundFailedException(String message) {
        super(message);
    }
}
