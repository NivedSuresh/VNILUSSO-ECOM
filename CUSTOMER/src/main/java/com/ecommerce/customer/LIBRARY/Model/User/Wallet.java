package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "wallet")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Wallet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double balance;
    @OneToOne @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;
}
