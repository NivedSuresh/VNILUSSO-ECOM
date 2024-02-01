package com.ecommerce.customer.Controller;


import com.ecommerce.customer.LIBRARY.Dtos.CartDto;
import com.ecommerce.customer.LIBRARY.Model.User.Cart;
import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.ProductsService.CouponService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    CartService cartService;
    CouponService couponService;

    public CartController(CartService cartService, CouponService couponService) {
        this.cartService = cartService;
        this.couponService = couponService;
    }

    @GetMapping
    public String getCart(Model model, Principal principal, HttpSession session){

        if(principal==null){
            CartDto cart = (CartDto) session.getAttribute("cart");
            if(cart!=null){
                session.setAttribute("cartSize", cart.getCartItems().size());
                model.addAttribute("cart", cart);
            }else
                session.removeAttribute("cartSize");
            return "/cart";
        }

        Cart cart = cartService.findByUsername(principal.getName());
        model.addAttribute("cart", cart);
        if(cart!=null){
            model.addAttribute("coupons", couponService.findAvailableForCustomer(cart));
        }
        session.setAttribute("cartSize", cart==null?0:cart.getCartItems().size());

        return "/cart";
    }

    @Transactional
    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long id, String size, Principal principal,
                            Model model, HttpSession session){

        if(principal==null){
            cartService.addItemToCartSession(id, session, size);
            return "redirect:/cart";
        }

        cartService.addItem(id, principal.getName(), 1, size);
        model.addAttribute("cart", cartService.findByUsername(principal.getName()));
        return "redirect:/cart";
    }

    @PostMapping("/remove/session/{id}")
    public String removeItem(@PathVariable(value = "id", required = false) String sessionItemId ,String size, HttpSession session, Principal principal){
        cartService.removeItem(null, sessionItemId, session, principal, size);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeItem(@PathVariable(value = "id", required = false) Long id, String size, HttpSession session, Principal principal){
        cartService.removeItem(id,null, session, principal, size);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("quantityPerItem") List<Integer> quantityPerItem,
                             @RequestParam(value = "cartItemsId", required = false) List<Long> cartItemsId
                             ,@RequestParam(value = "cartItemsIdSession", required = false) List<String> cartItemsIdSession,
                             String size,
                             Principal principal, HttpSession session){

        if(principal==null)cartService.updateCartForSession(cartItemsIdSession, quantityPerItem,session, size);
        else cartService.updateCart(principal.getName(), cartItemsId, quantityPerItem);
        return "redirect:/cart";
    }

    @PostMapping("/clear/{id}")
    public String clearCart(@PathVariable("id") Long id, Principal principal, HttpSession session){
        if(principal==null){
            CartDto cartDto = (CartDto) session.getAttribute("cart");
            cartDto.setCartItems(new HashMap<>());
            session.setAttribute("cart", cartDto);
        }
        else cartService.clearCart(id);
        return "redirect:/cart";
    }
}
