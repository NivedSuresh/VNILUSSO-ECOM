package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="order_id", referencedColumnName = "order_id")
    Order order;

    String size;
    Integer quantityPerItem;
    Double totalPrice;

    @Override
    public String toString() {
        return "\nProduct : " + product.getName() +'\n'+
                "Size : '" + size + '\n' +
                "Quantity : " + quantityPerItem +'\n'+
                "Total : " + totalPrice+'\n';
    }
}
