package com.ecommerce.admin.LIBRARY.Repository.UserRepos;

import com.ecommerce.admin.LIBRARY.Model.User.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAddressRepo extends JpaRepository<OrderAddress, Long> {
}
