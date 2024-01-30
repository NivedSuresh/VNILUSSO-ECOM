package com.ecommerce.admin.LIBRARY.Repository.UserRepos;

import com.ecommerce.admin.LIBRARY.Model.User.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, Long> {

    @Query("select true from Wallet  w where w.customer.email = :email")
    boolean existsByCustomerEmail(String email);

    @Query("select w from Wallet w where w.customer.email = :email")
    Wallet findByEmail(String email);

    @Modifying @Transactional
    @Query("update Wallet  w set w.balance = :balance where w.id = :id")
    void setWalletBalance(Long id, double balance);
}
