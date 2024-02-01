package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "task_offers")
@Data
public class TaskOffers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_offers_id")
    private Long id;
    private String offerName;
    private Boolean enabled;
}
