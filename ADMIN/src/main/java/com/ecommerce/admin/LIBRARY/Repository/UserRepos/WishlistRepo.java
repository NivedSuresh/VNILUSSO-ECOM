package com.ecommerce.admin.LIBRARY.Repository.UserRepos;

import com.ecommerce.admin.LIBRARY.Model.User.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WishlistRepo extends JpaRepository<Wishlist, Long> {

    Wishlist findByCustomerEmail(String email);

    @Modifying @Transactional
    @Query(value = "delete from wishlist_product where wishlist_id = :id and product_id = :productId", nativeQuery = true)
    void removeProductByProductId(Long id, Long productId);
}
