package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;
    private  LocalDate deliveryDate;
    private String orderStatus;
    private Double totalPrice;
    private Double tax;
    private int quantity;
    private String paymentMethod;
    private Boolean isAccepted;
    private Boolean isCancelled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "order")
    @JoinColumn(name = "order_address_id", referencedColumnName = "order_address_id")
    private OrderAddress orderAddress;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "order")
    private Payment payment;

    @OneToOne(mappedBy = "order")
    private ReturnRequest returnRequest;

    private Double deductedFromWallet;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Coupon coupon;

}
