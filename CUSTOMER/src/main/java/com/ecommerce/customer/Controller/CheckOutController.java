package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Dtos.AddressDto;
import com.ecommerce.customer.LIBRARY.Dtos.CartDto;
import com.ecommerce.customer.LIBRARY.Model.User.Cart;
import com.ecommerce.customer.LIBRARY.ProductsService.AddressService;
import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.ProductsService.OrderService;
import com.ecommerce.customer.LIBRARY.ProductsService.WalletService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CheckOutController {

    CartService cartService;
    AddressService addressService;
    OrderService orderService;
    CustomerService customerService;
    WalletService walletService;

    public CheckOutController(CartService cartService, AddressService addressService,
                              OrderService orderService, CustomerService customerService,
                              WalletService walletService) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.orderService = orderService;
        this.customerService = customerService;
        this.walletService = walletService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping("/checkout")
    public String checkoutView(Model model, HttpSession session, Principal principal){
        if(principal==null){
            CartDto cart = (CartDto) session.getAttribute("cart");

            if(cart==null || cart.getCartItems()==null || cart.getCartItems().isEmpty())
                return "redirect:/cart";

            session.removeAttribute("cart_id");
            model.addAttribute("totalAmount", cart.getTotalPrice());
            model.addAttribute("cartItems", cart.getCartItems().values());
        }else{
            Cart cart = cartService.findByUsername(principal.getName());

            if(cart==null || cart.getCartItems()==null || cart.getCartItems().isEmpty())
                return "redirect:/cart";

            BindingResult result  = (BindingResult) session.getAttribute("result");
            if(result!=null && result.hasErrors()){
                model.addAttribute("result", result);
                session.removeAttribute("result");
            }
            model.addAttribute("wallet", walletService.getWallet(principal.getName()));
            model.addAttribute("addressList", addressService.findAddressByCustomer(principal.getName()));
            model.addAttribute("addressDto", new AddressDto());
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("totalAmount", cart.getTotalPrice());
            model.addAttribute("cartId", cart.getId());
        }

        return "/checkout";
    }

    @PostMapping("/address/add")
    public String addAddress(@Valid AddressDto addressDto, BindingResult result,
                             Model model, Principal principal, HttpSession session){

        if(result.hasErrors()){
            session.setAttribute("result", result);
            return "redirect:/checkout";
        }

        addressService.saveAddress(principal.getName(), addressDto);
        return "redirect:/checkout";
    }

    @GetMapping("/orders")
    public String getAllOrders(Principal principal, Model model){
        model.addAttribute("orders", orderService.findOrdersByCustomer(customerService.findByEmail(principal.getName())));
        return "/PostAuth/orders";
    }


}
