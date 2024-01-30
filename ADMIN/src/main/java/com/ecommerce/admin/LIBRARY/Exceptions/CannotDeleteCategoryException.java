package com.ecommerce.admin.LIBRARY.Exceptions;

public class CannotDeleteCategoryException extends RuntimeException{
    public CannotDeleteCategoryException(String message) {
        super(message);
    }
}
