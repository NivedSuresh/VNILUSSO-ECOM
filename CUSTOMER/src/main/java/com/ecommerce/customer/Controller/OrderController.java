package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.ProductsService.AddressService;
import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.ProductsService.OrderService;
import com.ecommerce.customer.LIBRARY.ProductsService.RazorPayOrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/order")
@Controller
public class OrderController {

    CartService cartService;
    AddressService addressService;
    OrderService orderService;
    RazorPayOrderService razorPayOrderService;

    public OrderController(CartService cartService, AddressService addressService,
                           OrderService orderService, RazorPayOrderService razorPayOrderService) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.orderService = orderService;
        this.razorPayOrderService = razorPayOrderService;
    }

    @ResponseBody
    @PostMapping("/place/cod")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> data, HttpSession session,
                             Principal principal){

        Long addressId = null;
        if(data.containsKey("addressId")){
            try{addressId = Long.parseLong(data.get("addressId").toString());}
            catch (Exception e){e.printStackTrace();}
        }

        if(addressId!=null && !addressService.existsById(addressId))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Address null or doesn't exists");


        //complete use wallet balance
        orderService.placeOrderCod(addressId, principal.getName(), data.get("useWalletBalance").toString().equals("true"));
        session.removeAttribute("cartSize");

        return ResponseEntity.ok(data);

    }

    @GetMapping("details/{id}")
    public String viewOrderDetails(@PathVariable("id") Long id, Model model, Principal principal){
        Order order = orderService.findById(id, principal.getName());
        model.addAttribute("orderAddress", order.getOrderAddress());
        model.addAttribute("order", order);
        return "/PostAuth/orderDetails";
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id,  Principal principal){
        if(Objects.equals(orderService.getOrderPaymentMethod(id), "RAZORPAY"))
            razorPayOrderService.initiateRefund(id, false, false);
        else {
            orderService.cancelOrderCod(id, principal.getName());
        }
        return "redirect:/orders";
    }

    @PostMapping("/return/{id}")
    public String returnOrder(@PathVariable("id") Long id){
        if(orderService.existsById(id)){
            Order order = orderService.findById(id);
            orderService.returnOrder(order);
        }
        return "redirect:/order/details/"+id;
    }


}
