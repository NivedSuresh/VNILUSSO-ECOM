package com.ecommerce.admin.LIBRARY.ExceptionHandler;

import com.ecommerce.admin.LIBRARY.Exceptions.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@ControllerAdvice
public class OrderExceptionHandler {

    @GetMapping("/errorView")
    public String errorView(Model model, String message){
        model.addAttribute("errorMsg", message);
        return "exceptionProducts";
    }

    @ExceptionHandler(UnableToFindOrderException.class)
    public String UnableToFindOrderExceptionHandler(UnableToFindOrderException e, Model model){
        return errorView(model, e.getMessage());
    }

    @ExceptionHandler(UnableToMakeOrderException.class)
    public String UnableToMakeOrderExceptionHandler(UnableToMakeOrderException e, Model model){
        return errorView(model, e.getViewMessage());
    }

    @ExceptionHandler(InvalidStateException.class)
    public String InvalidStateExceptionHandler(InvalidStateException e, Model model){
        return errorView(model, e.getViewMessage());
    }

    @ExceptionHandler(RefundFailedException.class)
    public String RefundFailedExceptionHandler(RefundFailedException e, Model model){
        return errorView(model, e.getMessage());
    }

    @ExceptionHandler(OrderPastProcessingException.class)
    public String OrderPastProcessingExceptionHandler(OrderPastProcessingException e, Model model){
        return errorView(model, e.getViewMessage());
    }

    @ExceptionHandler(UnableToInitiateRefund.class)
    public String UnableToInitiateRefundHandler(UnableToInitiateRefund e, Model model){
        return errorView(model, e.getMessage());
    }
    @ExceptionHandler(UnableToInitiateReturn.class)
    public String UnableToInitiateReturnHandler(UnableToInitiateReturn e, Model model){
        return errorView(model, e.getMessage());
    }
    @ExceptionHandler(CouponExpiredException.class)
    public String CouponExpiredExceptionHandler(CouponExpiredException e, Model model){
        return errorView(model, e.getMessage());
    }

    @ExceptionHandler(CannotApplyCouponException.class)
    public String CannotApplyCouponExceptionHandler(CannotApplyCouponException e, Model model){
        return errorView(model, e.getMessage());
    }



}
