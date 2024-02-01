package com.ecommerce.customer.LIBRARY.Dtos;

import com.ecommerce.customer.LIBRARY.Model.User.Customer;
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
