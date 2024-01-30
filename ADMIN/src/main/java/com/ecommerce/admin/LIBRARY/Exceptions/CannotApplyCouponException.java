package com.ecommerce.admin.LIBRARY.Exceptions;

public class CannotApplyCouponException extends RuntimeException{
    public CannotApplyCouponException(String message) {
        super(message);
    }
}
