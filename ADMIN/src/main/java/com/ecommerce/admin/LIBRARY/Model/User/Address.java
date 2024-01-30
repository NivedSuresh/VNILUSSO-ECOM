package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_addresses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    Long id;

    String recipientName;
    String houseName;
    String addressLine;
    String phoneNumber;
    String zipcode;
    String state;
    String district;
    String city;
    String typeOfAddress;
    String notes;
    Boolean isDefault;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    Customer customer;
}
