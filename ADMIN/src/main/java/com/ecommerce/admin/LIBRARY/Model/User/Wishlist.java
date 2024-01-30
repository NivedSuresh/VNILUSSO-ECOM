package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Table(name = "wishlist")
@NoArgsConstructor @AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    Long id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "wishlist_product",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    Set<Product> products;

    Integer size;

    @OneToOne(fetch = FetchType.EAGER) @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    Customer customer;

    public Wishlist(Long id, Integer size, Customer customer) {
        this.id = id;
        this.products = new HashSet<>();
        this.size = size;
        this.customer = customer;
    }
}
