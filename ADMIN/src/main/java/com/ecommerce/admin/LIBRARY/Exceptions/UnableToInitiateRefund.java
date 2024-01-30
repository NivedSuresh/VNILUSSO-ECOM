package com.ecommerce.admin.LIBRARY.Exceptions;

public class UnableToInitiateRefund extends RuntimeException{
    public UnableToInitiateRefund(String message) {
        super(message);
    }
}
