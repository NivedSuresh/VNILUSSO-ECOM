package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter @Setter @ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;


    @NotNull(message = "Category cannot be null")
    private String category;

    @NotNull
    private boolean isDeleted;

    private Long orders;

    private Integer avg_price;

    private Integer products;

    private Double discountPercentage;

    public Category(String category) {
        this.category = category;
        this.isDeleted = false;
    }

}
