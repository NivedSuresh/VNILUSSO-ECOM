package com.ecommerce.customer.LIBRARY.ProductsService;

import com.ecommerce.customer.LIBRARY.Model.User.Wishlist;

public interface WishlistService {

    Wishlist findByCustomerEmail(String email);


    void addToWishlist(Long productId, String name);

    void removeFromWishlist(Long productId, String name);
}
