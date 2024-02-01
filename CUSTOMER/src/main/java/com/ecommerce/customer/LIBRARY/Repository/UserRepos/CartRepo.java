package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Cart;
import com.ecommerce.customer.LIBRARY.Model.User.Coupon;
import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
    Cart findByCustomer(Customer customer);
    Optional<Cart> findById(Long id);

    Cart findCartById(Long id);

    @Query("select c.cartItems from Cart c where c.id  = :id")
    Integer getCartSize(Long id);

    @Modifying @Transactional
    @Query("update Cart c set c.totalPrice = :totalPrice where c.id = :cartId")
    void setTotalPrice(@Param("cartId") Long cartId, @Param("totalPrice") Double totalPrice);

    @Modifying @Transactional
    @Query("update Cart c set c.totalItems = 0 where c.id = :id")
    void emptyTotalItems(Long id);

    @Query("select sum(ci.totalPrice) from CartItem  ci where ci.cart.id = :cartId")
    Double getCartTotalIncludeCouponDiscount(Long cartId);

    @Modifying @Transactional
    @Query("UPDATE Cart as c set c.coupon = :coupon where c.id = :cartId")
    void setCouponForCart(Long cartId, Coupon coupon);

    @Query("select sum(c.product.salePrice*c.quantity) from CartItem as c where c.cart.id = :cartId")
    Double getCartTotalExcludingCoupon(Long cartId);
}
