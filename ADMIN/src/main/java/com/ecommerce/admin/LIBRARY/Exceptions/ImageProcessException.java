package com.ecommerce.admin.LIBRARY.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="File processing failed")
public class ImageProcessException extends RuntimeException{
    String userMessage;
    public ImageProcessException(String message, String userMessage) {
        this.userMessage = userMessage;
    }
}
