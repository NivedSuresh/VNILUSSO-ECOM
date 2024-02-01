package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.ProductsService.CouponService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
public class CartCouponController {

    CouponService couponService;
    CartService cartService;

    public CartCouponController(CouponService couponService, CartService cartService) {
        this.couponService = couponService;
        this.cartService = cartService;
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(Principal principal, String couponCode){
        if(principal==null) return "redirect:/login";
        couponService.applyCoupon(principal.getName(), couponCode);
        return "redirect:/cart";
    }

    @GetMapping("/coupon/remove")
    public String removeCoupon(Principal principal){
        if(principal==null) return "redirect:/cart";
        couponService.removeCoupon(principal.getName());
        return "redirect:/cart";
    }
}
