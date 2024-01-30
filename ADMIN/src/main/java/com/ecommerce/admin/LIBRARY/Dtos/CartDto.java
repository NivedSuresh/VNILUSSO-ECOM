package com.ecommerce.admin.LIBRARY.Dtos;

import com.ecommerce.admin.LIBRARY.Model.User.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter @Getter
public class CartDto {
    private Long id;
    private Customer customer;
    private double totalPrice;
    private int totalItems;
    private Map<String, CartItemDto> cartItems;
}
