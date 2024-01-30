package com.ecommerce.admin.LIBRARY.ProductsService;

import com.ecommerce.admin.LIBRARY.Model.User.Cart;
import com.ecommerce.admin.LIBRARY.Model.User.Coupon;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;

public interface CartService {

    Double getCartTotalExcludeCouponDiscount(Long cartId);
    Integer getCartSize(String name);
    Cart findByUsername(String username);
    void addItem(Long id, String email, Integer quantity, String size);
    void removeItem(Long id, String sessionItemId, HttpSession session, Principal principal, String size);

    void updateCart(String email, List<Long> cartItemsId, List<Integer> quantityPerItem);

    void addItemToCartSession(Long id, HttpSession session, String size);

    void updateCartForSession(List<String> cartItemsId, List<Integer> quantityPerItem, HttpSession session, String size);

    void clearCart(Long id);

    Integer cartSize(Long id);

    boolean existsById(Long cartId);

    Cart findCartById(Long id);

    Double getCartTotal(Long cartId);
    void updateCartItemsPriceAfterNewCoupon(Long cartId, Double couponDiscountPercentage);
    void setCouponForCart(Long cartId, Coupon coupon);

    void setCartTotalPrice(Long cartId, Double totalPrice);

    Double calculateDiscountPercentage(double totalPrice, Double maxDiscountAmount);

    void dealWithSessionCartIfExists(HttpSession session, String email);
}
