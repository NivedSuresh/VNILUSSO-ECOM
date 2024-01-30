package com.ecommerce.admin.LIBRARY.ExceptionHandler;

import com.ecommerce.admin.LIBRARY.Exceptions.EmailNullException;
import com.ecommerce.admin.LIBRARY.Exceptions.InvalidTokenException;
import com.ecommerce.admin.LIBRARY.Exceptions.OtpInvalidException;
import org.springframework.mail.MailSendException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class UtilExceptionHandler {

    @GetMapping("/errorView")
    public String errorView(Model model, String message){
        model.addAttribute("errorMsg", message);
        return "exceptionProducts";
    }

    @ExceptionHandler(MailSendException.class)
    public String MailSenderExceptionHandler(MailSendException e, Model model){
        return errorView(model, e.getMessage());
    }

    @ExceptionHandler(OtpInvalidException.class)
    public String OtpGenerationExceptionHandler(OtpInvalidException e, Model model){
        return errorView(model, e.getViewMessage());
    }


    @ExceptionHandler(InvalidTokenException.class)
    public String TokenAlreadyExistsExceptionHandler(InvalidTokenException e, Model model){
        return errorView(model, e.getViewMessage());
    }

    @ExceptionHandler(EmailNullException.class)
    public String EmailNullExceptionHandler(EmailNullException e, Model model){
        return errorView(model, e.getViewMessage());
    }


}
