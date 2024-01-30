package com.ecommerce.admin.LIBRARY.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Invalid Otp or is Expired")
public class OtpInvalidException extends RuntimeException{
    String viewMessage;
    public OtpInvalidException(String message, String viewMessage){
        super(message);
        this.viewMessage = viewMessage;
    }
}
