package com.ecommerce.customer.LIBRARY.Exceptions;

import lombok.Getter;

@Getter
public class OrderPastProcessingException extends RuntimeException {
    String viewMessage;

    public OrderPastProcessingException(String message, String viewMessage) {
        super(message);
        this.viewMessage = viewMessage;
    }
}
