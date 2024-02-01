package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Payment findPaymentByRazorpayOrderId(String id);

    @Query("update Payment p set p.status = :status where p.order.id =:id")
    void setPaymentStatus(Long id, String status);

}