package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    @Transactional @Modifying
    @Query("delete from CartItem where id = :id")
    void removeItem(Long id);

    @Query("select c.totalPrice from CartItem as c where c.id= :id")
    Double getCartItemPriceById(Long id);

    @Transactional @Modifying
    @Query("delete from CartItem where cart.id = :id")
    void deleteByCartId(Long id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE cart_item AS ci SET total_price = " +
            "ROUND(CAST((select (p.sale_price * ci.quantity) * :decimalToBeMultipliedWith " +
            "from products as p where ci.product_id = p.product_id) AS NUMERIC), 2) " +
            "where cart_id = :cartId", nativeQuery = true)
    void updateCartItemsPriceAfterNewCoupon(Long cartId, Double decimalToBeMultipliedWith);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cart_item AS ci " +
            "SET total_price = CAST(ROUND(ci.quantity * p.sale_price) AS NUMERIC(10, 2)) " +
            "FROM products p " +
            "WHERE ci.product_id = p.product_id AND ci.cart_id = :id", nativeQuery = true)
    void resetCartItemsPrice(@Param("id") Long id);

}
