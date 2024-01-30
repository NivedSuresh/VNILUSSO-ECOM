package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.ProductsService.OrderService;
import com.ecommerce.admin.LIBRARY.ProductsService.RazorPayOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/order/")
public class OrderController {

    RazorPayOrderService razorPayOrderService;
    OrderService orderService;

    public OrderController(RazorPayOrderService razorPayOrderService, OrderService orderService) {
        this.razorPayOrderService = razorPayOrderService;
        this.orderService = orderService;
    }

    @GetMapping("{id}")
    public String orderDetails(@PathVariable Long id, Model model){
        model.addAttribute("order", orderService.findById(id));
        return "PostAuth/orderDetails";
    }

    @Transactional
    @PostMapping("/update/{id}")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status){
        if(Objects.equals(orderService.getOrderStatus(id), status))
            return "redirect:/order/"+id;
        if(orderService.existsById(id) && !orderService.isOrderCancelled(id)){
            if(Objects.equals(status, "CANCELLED") &&
                    Objects.equals(orderService.getOrderPaymentMethod(id), "RAZORPAY")){
                razorPayOrderService.initiateRefund(id, true, false);
            }
            orderService.setOrderStatus(status.toUpperCase(), id, orderService.findById(id));
            return "redirect:/order/"+id+"?success";
        }
        return "redirect:/order/"+id+"?error";
    }

    @Transactional
    @PostMapping("/accept_return_refund/{id}")
    public String initiateRefund(@PathVariable("id") Long orderId){
        String orderStatus = orderService.getOrderStatus(orderId);

        if(Objects.equals(orderStatus, "RETURN")){
            String paymentMethod = orderService.getOrderPaymentMethod(orderId);

            if(Objects.equals(paymentMethod, "RAZORPAY")){
                razorPayOrderService.initiateRefund(orderId, true, true);
            }
            else if(Objects.equals(paymentMethod, "COD"))
                orderService.initiateRefundForCodReturn(orderId);
        }

        return "redirect:/order/"+orderId;
    }



}
