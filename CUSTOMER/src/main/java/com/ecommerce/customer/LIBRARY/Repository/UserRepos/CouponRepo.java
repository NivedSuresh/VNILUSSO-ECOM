package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepo extends JpaRepository<Coupon, Long> {
    boolean existsByCoupon(String coupon);
    Coupon findCouponByCoupon(String coupon);

//    "select count(cc) from customer_coupons as cc where customer_id = :id and coupon_id = :couponId"
@Query(value = "SELECT c.* FROM coupons AS c WHERE c.is_active = true AND c.expiry_date >= :now " +
        "AND c.min_spend <= :totalPrice AND c.usage_allowed_per_customer > (" +
        "SELECT COUNT(cc) FROM customer_coupons AS cc WHERE cc.customer_id = :customerId " +
        "AND cc.coupon_id = c.coupon_id)",
        nativeQuery = true)
List<Coupon> findAvailableCouponsForCustomer(LocalDateTime now, double totalPrice, Long customerId);
}
