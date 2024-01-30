package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "products")
@Getter @Setter @Builder @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NotNull
    private String name;

    @NotNull @ElementCollection(fetch = FetchType.EAGER)
    List<String> size;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;

    @NotNull
    private double costPrice;
    @NotNull
    private double salePrice;

    @NotNull
    private String description;

    @NotNull
    private String brand;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images_url", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> imagesUrls;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;

    private boolean isDeleted;

    private boolean onSale;

}
