package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestRepo extends JpaRepository<ReturnRequest, Long> {


    ReturnRequest findByOrderId(Long orderId);
}
