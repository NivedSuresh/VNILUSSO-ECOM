package com.ecommerce.admin.LIBRARY.Repository.UserRepos;

import com.ecommerce.admin.LIBRARY.Model.User.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestRepo extends JpaRepository<ReturnRequest, Long> {


    ReturnRequest findByOrderId(Long orderId);
}
