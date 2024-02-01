package com.ecommerce.customer.LIBRARY.Dtos;

import com.ecommerce.customer.LIBRARY.Model.User.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CartItemDto {

    private String id;
    private CartDto cart;
    private Product product;
    private int quantity;
    private double unitPrice;
    private String size;

    public CartItemDto(CartDto cart, Product product, int quantity, double unitPrice, String size) {
        this.id = product.getId()+"-"+size;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.size=size;
    }
}
