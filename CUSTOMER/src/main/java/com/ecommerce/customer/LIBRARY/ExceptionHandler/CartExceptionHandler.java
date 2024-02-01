package com.ecommerce.customer.LIBRARY.ExceptionHandler;

import com.ecommerce.customer.LIBRARY.Exceptions.UnableToFindCartException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class CartExceptionHandler {

    @GetMapping("/errorView")
    public String errorView(Model model, String message){
        model.addAttribute("errorMsg", message);
        return "exceptionProducts";
    }

    @ExceptionHandler(UnableToFindCartException.class)
    public String UnableToFindCartException(UnableToFindCartException u, Model model){
        return errorView(model, u.getMessage());
    }


}
