package com.ecommerce.admin.LIBRARY.Dtos;

import java.util.List;

public class ErrorResponse {
    List<String> errors;
    public ErrorResponse(List<String> errors) {
        this.errors = errors;
    }
}
