package com.ecommerce.customer.LIBRARY.Exceptions;

public class UnableToFindCartException extends RuntimeException{

    public UnableToFindCartException(String message) {
        super(message);
    }
}
