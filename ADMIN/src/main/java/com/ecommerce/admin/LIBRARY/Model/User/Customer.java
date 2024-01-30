package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter @Setter
public class Customer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(unique = true)
    private String email;


    private String username;

    private String password;

    @NotNull(message = "User role cannot be null")
    private String role;

    private String phoneNumber;

    @NotNull(message = "Constraint error, isDeleted was not set during account creation/updation.")
    private boolean isDeleted;

    @NotNull
    private boolean isBlocked;

    @NotNull
    private Date createdOn;

    private Date updatedOn;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "customer", fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Order> orders;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "customer", fetch = FetchType.LAZY)
    Wishlist wishlist;

    @OneToOne(mappedBy = "customer", fetch = FetchType.EAGER)
    Wallet wallet;

    @ManyToMany
    @JoinTable(name = "customer_Coupons",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id"))
    private List<Coupon> coupons;

}
