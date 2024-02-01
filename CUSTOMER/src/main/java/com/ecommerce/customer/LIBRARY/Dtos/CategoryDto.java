package com.ecommerce.customer.LIBRARY.Dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryDto {

    @NotNull
    private String category;

    @NotNull
    private boolean isDeleted;

}
