package com.ecommerce.customer.LIBRARY.Exceptions;

public class SameOrderException extends RuntimeException{

    public SameOrderException(String message) {
        super(message);
    }
}
