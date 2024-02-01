package com.ecommerce.customer.LIBRARY.Exceptions;

public class CannotApplyCouponException extends RuntimeException{
    public CannotApplyCouponException(String message) {
        super(message);
    }
}
