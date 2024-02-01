package com.ecommerce.customer.LIBRARY.Exceptions;

public class CannotDeleteCategoryException extends RuntimeException{
    public CannotDeleteCategoryException(String message) {
        super(message);
    }
}
