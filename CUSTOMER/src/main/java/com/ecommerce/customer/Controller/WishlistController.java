package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.ProductsService.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public String wishlist(Model model, Principal principal){
        model.addAttribute("wishlist", wishlistService.findByCustomerEmail(principal.getName()));
        return "/PostAuth/wishlist";
    }

    @GetMapping("add/{id}")
    public String addToWishlist(@PathVariable("id") Long productId, Principal principal){
        wishlistService.addToWishlist(productId, principal.getName());
        return "redirect:/wishlist";
    }

    @GetMapping("/remove/{id}")
    public String removeFromWishlist(@PathVariable("id") Long productId, Principal principal){
        wishlistService.removeFromWishlist(productId, principal.getName());
        return "redirect:/wishlist";
    }

}
