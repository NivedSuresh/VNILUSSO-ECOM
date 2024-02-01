package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_address")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderAddress {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_address_id")
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

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    Order order;

    @Override
    public String toString() {
        return "OrderAddress \uD83C\uDFE1 : \n\n" +
                "Recipient Name : '" + recipientName + '\n' +
                "HouseName : " + houseName + '\n' +
                "Address Line : '" + addressLine + '\n' +
                "Phone Number : '" + phoneNumber + '\n' +
                "Zipcode : " + zipcode + '\n' +
                "State : " + state + '\n' +
                "District : '" + district + '\n' +
                "City : " + city + '\n' +
                "Type Of Address : " + typeOfAddress + '\n' +
                "Notes : " + notes + '\n';
    }
}
