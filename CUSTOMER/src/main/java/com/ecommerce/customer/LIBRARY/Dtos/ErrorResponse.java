package com.ecommerce.customer.LIBRARY.Dtos;

import java.util.List;

public class ErrorResponse {
    List<String> errors;
    public ErrorResponse(List<String> errors) {
        this.errors = errors;
    }
}
