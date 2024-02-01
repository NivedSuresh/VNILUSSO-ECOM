package com.ecommerce.customer.LIBRARY.Exceptions;

public class CouponExpiredException  extends RuntimeException{
    public CouponExpiredException(String message) {
        super(message);
    }
}
