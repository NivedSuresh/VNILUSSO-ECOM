package com.ecommerce.customer.LIBRARY.Model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "payment_id")
    private Long id;

    private String razorpayOrderId;
    private Double amount;
    private String receipt;
    private String status;

    @Column(name = "razorpay_payment_id")
    private String paymentId;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    private Boolean refundInitiated;

    private String refundStatus;

    private Double deductedFromWallet;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRazorpay Order Id : '").append(razorpayOrderId).append("'\n");
        sb.append("Amount $: ").append(amount).append("\n");
        sb.append("Receipt : '").append(receipt).append("'\n");
        sb.append("Payment Status : '").append(status).append("'\n");
        sb.append("Payment Id : '").append(paymentId).append("'\n");
        sb.append("Refund Initiated : ").append(refundInitiated).append("\n");
        sb.append("Refund Status : '").append(refundStatus).append("'\n");
        sb.append("Deducted From Wallet : $").append(deductedFromWallet==null?0.0:deductedFromWallet);
        return sb.toString();
    }
}
