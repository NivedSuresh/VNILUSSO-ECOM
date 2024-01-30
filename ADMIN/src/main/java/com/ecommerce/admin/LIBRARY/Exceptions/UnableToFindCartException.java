package com.ecommerce.admin.LIBRARY.Exceptions;

public class UnableToFindCartException extends RuntimeException{

    public UnableToFindCartException(String message) {
        super(message);
    }
}
