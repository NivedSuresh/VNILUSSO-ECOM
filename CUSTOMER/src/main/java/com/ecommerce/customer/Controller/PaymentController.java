package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.ProductsService.RazorPayOrderService;
import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class PaymentController {

    RazorPayOrderService razorPayOrderService;

    public PaymentController(RazorPayOrderService razorPayOrderService) {
        this.razorPayOrderService = razorPayOrderService;
    }

    @ResponseBody
    @PostMapping("/payment/razorpay")
    public String razorPayHandler(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {
        return razorPayOrderService.createOrderForRazorpay(data, principal).toString();
    }

    @PostMapping("/payment/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data, Principal principal){
        System.out.println(data);
        razorPayOrderService.updateOrder(data, principal);
        return ResponseEntity.ok(Map.of("msg", "updated"));
    }



}
