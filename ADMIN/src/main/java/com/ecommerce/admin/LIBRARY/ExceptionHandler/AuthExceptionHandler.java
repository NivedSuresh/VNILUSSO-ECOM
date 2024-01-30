package com.ecommerce.admin.LIBRARY.ExceptionHandler;

import com.ecommerce.admin.LIBRARY.Exceptions.*;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class AuthExceptionHandler {

    @GetMapping("/errorView")
    public String errorView(Model model, String message){
        model.addAttribute("errorMsg", message);
        return "exceptionProducts";
    }

    @ExceptionHandler(EmailNullException.class)
    public String EmailNullExceptionHandler(EmailNullException e, Model model){
        return errorView(model, e.getViewMessage());
    }

    @ExceptionHandler(OtpInvalidException.class)
    public String OtpGenerationExceptionHandler(OtpInvalidException e, Model model){
        return errorView(model, e.getViewMessage());
    }


    @ExceptionHandler(InvalidTokenException.class)
    public String TokenAlreadyExistsExceptionHandler(InvalidTokenException e, Model model){
        return errorView(model, e.getViewMessage());
    }

    @ExceptionHandler(SessionExpiredException.class)
    public String SessionExpiredExceptionHandler(SessionExpiredException s, Model model){
        return errorView(model, s.getViewMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String HttpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e, Model model){
        return errorView(model, "The page you're looking for is Invalid or might have been removed(Method Not Supported).");
    }
    @ExceptionHandler(CustomerBlockedException.class)
    public String customerBlockedExceptionHandler(CustomerBlockedException ex, Model model){
        return errorView(model, ex.getViewMessage());
    }
    @ExceptionHandler(TokenGenerationException.class)
    public String TokenGenerationExceptionHandler(TokenGenerationException t, Model model){
        return errorView(model, t.getViewMessage());
    }
    @ExceptionHandler(InvalidLoginMethodException.class)
    public String InvalidLoginMethodExceptionHandler(InvalidLoginMethodException e, Model model){
        return errorView(model, e.getMessage());
    }


}
