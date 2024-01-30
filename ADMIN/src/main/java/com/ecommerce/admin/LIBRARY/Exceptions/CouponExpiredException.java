package com.ecommerce.admin.LIBRARY.Exceptions;

public class CouponExpiredException  extends RuntimeException{
    public CouponExpiredException(String message) {
        super(message);
    }
}
