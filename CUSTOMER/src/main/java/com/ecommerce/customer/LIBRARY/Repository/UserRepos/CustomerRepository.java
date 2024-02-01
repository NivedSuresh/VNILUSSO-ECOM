package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Cart;
import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import com.ecommerce.customer.LIBRARY.Model.User.Wallet;
import com.ecommerce.customer.LIBRARY.Model.User.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByEmail(String username);

    boolean existsByEmail(String email);

    @Query("select c.isBlocked from Customer as c where c.email = :email")
    boolean isBlocked(String email);

    @Transactional @Modifying
    @Query("update Customer as c set c.isBlocked = true where c.id = :id")
    void blockCustomer(Long id);

    @Query("select c.isDeleted from Customer as c where c.email = :email")
    boolean isDeleted(String email);

    @Transactional @Modifying
    @Query("update Customer c set c.isBlocked = false where c.id = :id")
    void unblockUser(Long id);

    @Query("select c.cart from Customer as c where c.email  = :email")
    Cart findCart(String email);

    @Query("select c.cart.id from Customer c where c.email = :email")
    Long findCartId(String email);

    @Modifying @Transactional
    @Query(value = "update Customer  c set c.phoneNumber = :phoneNumber where c.email = :email")
    void updatePhoneNumber(String email, String phoneNumber);

    @Modifying @Transactional
    @Query(value = "update Customer  c set c.username = :username where c.email = :email")
    void updateUserName(String email, String username);

    @Modifying @Transactional
    @Query(value = "update Customer c set c.email = :newEmail where c.email = :currentEmail")
    void updateEmail(String newEmail, String currentEmail);

    @Transactional @Modifying
    @Query(value = "update Customer c set c.password = :password where c.email = :email")
    void changePassword(String email, String password);

    @Query("select c.wishlist from Customer c where c.email = :email")
    Wishlist getWishlist(String email);

    @Query("select c.wallet from Customer c where c.email = :email")
    Wallet getWallet(String email);

    @Query(value = "select count(cc) from customer_coupons as cc where customer_id = :id and coupon_id = :couponId", nativeQuery = true)
    Integer couponUsedCountByCustomer(Long id, Long couponId);

    @Query("select c.role from Customer c where c.email = :email")
    String getCustomerAuthority(String email);
}
