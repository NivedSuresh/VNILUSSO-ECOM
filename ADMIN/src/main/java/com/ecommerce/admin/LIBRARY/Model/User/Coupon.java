package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "coupons", uniqueConstraints = @UniqueConstraint(name = "u_c",columnNames ="coupon"))
@Entity
public class Coupon {
    @Column(name = "coupon_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon")
    private String coupon;
    private Double discountPercentage;
    private Double maxDiscountAmount;
    private Integer usageAllowedPerCustomer;

    @ManyToMany(mappedBy = "coupons")
    private Set<Customer> customers;

    @Column(name = "expiry_date")
    LocalDateTime expiryDate;

    private Boolean isActive;
    private Double minSpend;
}
