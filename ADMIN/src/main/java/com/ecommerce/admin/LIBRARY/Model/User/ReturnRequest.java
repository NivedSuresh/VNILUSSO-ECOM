package com.ecommerce.admin.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "return_requests")
@Entity
public class ReturnRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Boolean returnRequest;
    private Boolean returnAccepted;
    private Date requestDate;
    private Date returnedDate;
    private String paymentMethod;
    private Double refundAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

}
