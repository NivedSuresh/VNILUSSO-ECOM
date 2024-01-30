package com.ecommerce.admin.LIBRARY.ProductsService;


import com.ecommerce.admin.LIBRARY.Model.User.Payment;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import java.security.Principal;
import java.util.Map;

public interface RazorPayOrderService {
    Order createOrderForRazorpay(Map<String, Object> data, Principal principal) throws RazorpayException;

    void updateOrder(Map<String, Object> data, Principal principal);

    void placeOrderRazorPay(String name, Payment payment, long addressId);

    void initiateRefund(Long OrderId, boolean admin, boolean isReturn);
}
