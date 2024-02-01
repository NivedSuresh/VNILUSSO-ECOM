package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity @Getter @Setter @ToString
@Table(name = "cart")
public class Cart {
    @Id @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    private double totalPrice;
    private Integer totalItems;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cart")
    private Set<CartItem> cartItems;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id")
    private Coupon coupon;

    public Cart() {
        this.totalPrice = 0.0;
        this.totalItems = 0;
        this.cartItems = new HashSet<>();
        this.coupon=null;
    }
}
