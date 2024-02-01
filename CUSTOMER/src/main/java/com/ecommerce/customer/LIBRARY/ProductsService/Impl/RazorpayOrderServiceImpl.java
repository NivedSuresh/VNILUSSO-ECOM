package com.ecommerce.customer.LIBRARY.ProductsService.Impl;

import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Exceptions.OrderPastProcessingException;
import com.ecommerce.customer.LIBRARY.Exceptions.RefundFailedException;
import com.ecommerce.customer.LIBRARY.Exceptions.UnableToMakeOrderException;
import com.ecommerce.customer.LIBRARY.Model.User.*;
import com.ecommerce.customer.LIBRARY.ProductsService.*;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.*;
import com.ecommerce.customer.LIBRARY.Service.MailService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Service
public class RazorpayOrderServiceImpl implements RazorPayOrderService {

    CartService cartService;
    PaymentRepo paymentRepo;
    AddressService addressService;
    OrderService orderService;
    OrderAddressRepo orderAddressRepo;
    MailService mailService;
    OrderRepo orderRepo;
    CartRepo cartRepo;
    CartItemRepo cartItemRepo;
    ReturnRequestRepo returnRequestRepo;
    WalletService walletService;
    WalletRepo walletRepo;


    private static final String RAZORPAY_API_KEY = "rzp_test_7689jAxqpAIZJL";

    public RazorpayOrderServiceImpl(CartService cartService, PaymentRepo paymentRepo,
                                    AddressService addressService, OrderService orderService,
                                    OrderAddressRepo orderAddressRepo,
                                    OrderRepo orderRepo, MailService mailService, CartRepo cartRepo,
                                    CartItemRepo cartItemRepo, ReturnRequestRepo returnRequestRepo,
                                    WalletService walletService, WalletRepo walletRepo) {

        this.cartService = cartService;
        this.paymentRepo = paymentRepo;
        this.addressService = addressService;
        this.orderService = orderService;
        this.orderAddressRepo = orderAddressRepo;
        this.orderRepo = orderRepo;
        this.mailService = mailService;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.returnRequestRepo = returnRequestRepo;
        this.walletService = walletService;
        this.walletRepo = walletRepo;
    }

    @Override
    public com.razorpay.Order createOrderForRazorpay(Map<String, Object> data, Principal principal) throws RazorpayException {

        try{

            RazorpayClient razorpayClient = null;
            try {
                razorpayClient = new RazorpayClient(RAZORPAY_API_KEY,"bVXzzMy5b5aUtBnsYV8DCLlF");
            } catch (Exception e) {
                e.printStackTrace();
                throw new UnableToMakeOrderException("","Couldn't make order as payment gateway failed to respond, try again after sometime!");
            }

            Cart cart = cartService.findByUsername(principal.getName());

            if(cart.getId()!=Integer.parseInt(data.get("cartId").toString()) ||
                    cart.getTotalPrice()!=Double.parseDouble(data.get("amount").toString())){
                throw new UnableToMakeOrderException("", "!Couldn't fetch cart details");
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("currency", "INR");
            jsonObject.put("receipt", "txn_vnilusso_");

            Payment payment = new Payment();
            Double balance = walletService.getBalance(cart.getCustomer());

            if(data.get("useWalletBalance").toString().equals("true") &&
                    balance>0.0 && balance<=cart.getTotalPrice()){
                payment.setAmount((double) Math.round((cart.getTotalPrice()*1.18) - balance));
                payment.setDeductedFromWallet(balance);}
            else {
                payment.setAmount((double)Math.round(cart.getTotalPrice()*1.18));
                payment.setDeductedFromWallet(0.0);}


            jsonObject.put("amount", Math.round(payment.getAmount()*100*80));
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(jsonObject);

            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setAmount(Double.parseDouble(razorpayOrder.get("amount").toString())/100/80);
            payment.setStatus("CREATED");
            payment.setPaymentId(null);
            payment.setOrder(null);
            payment.setReceipt(razorpayOrder.get("receipt").toString()+razorpayOrder.get("id").toString());
            payment.setRefundInitiated(false);
            payment.setRefundStatus("NEVER");

            paymentRepo.save(payment);

            return razorpayOrder;
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof UnableToMakeOrderException || e instanceof InvalidStateException)
                throw e;
            throw new UnableToMakeOrderException("","Unable to place order, try after sometime!");
        }

    }

    @Transactional
    @Override
    public void updateOrder(Map<String, Object> data, Principal principal) {
        try{
            Payment payment = paymentRepo.findPaymentByRazorpayOrderId(data.get("razorpay_order_id").toString());
            if(payment==null)
                throw new InvalidStateException("","Unable to proceed");

            payment.setStatus("PAID");
            payment.setPaymentId(data.get("razorpay_payment_id").toString());

            placeOrderRazorPay(principal.getName(), payment, Long.parseLong(data.get("addressId").toString()));
        }catch (Exception e){
            throw new InvalidStateException("","Couldn't Update order details");
        }

    }

    @Transactional
    @Override
    public void placeOrderRazorPay(String email, Payment payment, long addressId) {

        try{
            Cart cart = cartService.findByUsername(email);

            if(cart==null || cart.getCartItems()==null || cart.getCartItems().isEmpty())
                throw new UnableToMakeOrderException("","Cart cannot be empty");

            if(payment.getAmount()+payment.getDeductedFromWallet()<cart.getTotalPrice())
                throw new InvalidStateException("","Order cannot be placed as price in the cart doesn't match with the paid amount");

            if(cart.getCustomer() == null)
                throw new InvalidStateException("", "Couldn't place the Order");

            if(orderService.validateCartItemQuantity(cart))
                throw new UnableToMakeOrderException("", "Lesser number of stock available for the items in your Cart. Update your cart before trying again");


            Wallet wallet = cart.getCustomer().getWallet();
            wallet.setBalance(wallet.getBalance()-payment.getDeductedFromWallet());
            walletService.saveWallet(wallet);

            Order order = new Order(null, LocalDate.now(), LocalDate.now().plusDays(7), "PROCESSING", (double)Math.round(cart.getTotalPrice()*1.18),
                    (double) Math.round(cart.getTotalPrice()*0.18), cart.getTotalItems(), "RAZORPAY", true, false, cart.getCustomer(),
                    new ArrayList<>(), null, payment, null, payment.getDeductedFromWallet(), cart.getCoupon());
            order.setOrderItems(orderService.cartItemsToOrderItems(cart.getCartItems(), order));

            if(cart.getCoupon()!=null){
                List<Coupon> customerUsedCoupons = cart.getCustomer().getCoupons();
                if(customerUsedCoupons==null){
                    customerUsedCoupons=new ArrayList<>();
                }
                customerUsedCoupons.add(cart.getCoupon());
                cart.setCoupon(null);
            }

            Order saved = orderRepo.save(order);
            payment.setOrder(saved);
            paymentRepo.save(payment);
            OrderAddress orderAddress = addressService.addressDtoToOrderAddress(addressService.findAddressById(addressId), saved);
            orderAddressRepo.save(orderAddress);

            cartItemRepo.deleteByCartId(cart.getId());
            cart.setTotalItems(0);
            cart.setTotalPrice(0.0);
            cartRepo.save(cart);

        mailService.sendMail(email, mailService.orderPlacedMailStructure(order));
        }catch (Exception e){
            paymentRepo.deleteById(payment.getId());
            if(e instanceof UnableToMakeOrderException || e instanceof InvalidStateException)
                throw e;

            throw e;
        }


    }

    @Transactional
    @Override
    public void initiateRefund(Long orderId, boolean admin, boolean isReturn){
        try{

            Order order = validateAndGetOrder(admin, orderRepo.findById(orderId));

            if(isReturn){
                ReturnRequest returnRequest = order.getReturnRequest();

                if(returnRequest==null || returnRequest.getReturnAccepted() || !returnRequest.getReturnRequest())
                    throw new RefundFailedException("Cannot initiate refund as request is already processed or request doesn't exist.)");

                returnRequest.setReturnedDate(new Date());
                returnRequest.setReturnAccepted(true);

                returnRequestRepo.save(returnRequest);
            }

            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_API_KEY, "bVXzzMy5b5aUtBnsYV8DCLlF");
            String paymentId = order.getPayment().getPaymentId();

            Payment payment = order.getPayment();

            //payment contains the amount of total excluding deducted from wallet
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount",payment.getAmount().intValue()*80*100);
            refundRequest.put("speed","optimum");
            refundRequest.put("receipt",order.getPayment().getReceipt());

            Refund refund = razorpay.payments.refund(paymentId,refundRequest);


            if(!Objects.equals(payment.getStatus(), "PAID"))
                throw new InvalidStateException("","Cannot initiate Refund as payment was not made");

            if(Objects.equals(refund.get("status").toString(), "failed")) {payment.setRefundStatus("FAILED");}
            if(Objects.equals(refund.get("status").toString(), "pending") ||
                    Objects.equals(refund.get("status").toString(), "processed"))
                payment.setRefundStatus("REFUND INITIATED");

            payment.setRefundInitiated(true);

            if(order.getPayment().getDeductedFromWallet()>0.0){
                walletService.initiateWalletRefund(order, order.getPayment().getDeductedFromWallet());
            }

            paymentRepo.save(payment);
            orderService.putProductsBackAndCancelUnlessReturn(order, order.getCustomer().getEmail(), admin, isReturn);

        }catch (RazorpayException e){e.printStackTrace();}
        catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException || e instanceof OrderPastProcessingException ||
                    e instanceof RefundFailedException) throw e;
        }
    }

    @NotNull
    private static Order validateAndGetOrder(boolean admin, Optional<Order> optionalOrder) {
        Order order=null;

        if(optionalOrder.isPresent()) order = optionalOrder.get();
        else throw new InvalidStateException("","");

        if(!Objects.equals(order.getPaymentMethod(), "RAZORPAY")) throw new InvalidStateException("","Invalid state!");

        if(!Objects.equals(order.getOrderStatus(), "PROCESSING") && !admin ||
                Objects.equals(order.getOrderStatus(), "DELIVERED") && !admin)
            throw new OrderPastProcessingException("","Unable to process your Request!, " +
                    "contact customer service for more info.");

        if(order.getPayment()==null) throw new InvalidStateException("","Invalid state");

        String refundStatus = order.getPayment().getRefundStatus();

        if(Objects.equals(refundStatus, "REFUND INITIATED"))
            throw new RefundFailedException("Refund request was made, contact vnilusso@gmail.com for more details if refund was not made even after a day!");

        return order;
    }
}
