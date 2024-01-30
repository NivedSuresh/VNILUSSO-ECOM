package com.ecommerce.admin.LIBRARY.Exceptions;

import lombok.Getter;

@Getter
public class UnableToFindOrderException extends RuntimeException{
    public UnableToFindOrderException(String message) {
        super(message);
    }
}
