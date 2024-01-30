package com.ecommerce.admin.LIBRARY.ProductsService.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.AddressDto;
import com.ecommerce.admin.LIBRARY.Dtos.OrderFilterDto;
import com.ecommerce.admin.LIBRARY.Exceptions.*;
import com.ecommerce.admin.LIBRARY.Model.User.*;
import com.ecommerce.admin.LIBRARY.ProductsService.*;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.*;
import com.ecommerce.admin.LIBRARY.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    CartService cartService;
    AddressService addressService;
    OrderRepo orderRepo;
    OrderItemRepo orderItemRepo;
    ProductService productService;
    OrderAddressRepo orderAddressRepo;
    MailService mailService;
    CartRepo cartRepo;
    CartItemRepo cartItemRepo;
    PaymentRepo paymentRepo;
    ReturnRequestRepo returnRequestRepo;
    WalletService walletService;
    WalletRepo walletRepo;


    @Autowired
    public OrderServiceImpl(CartService cartService, AddressService addressService,
                            OrderRepo orderRepo, OrderItemRepo orderItemRepo,
                            OrderAddressRepo orderAddressRepo, ProductService productService,
                            MailService mailService, CartRepo cartRepo, CartItemRepo cartItemRepo,
                            ReturnRequestRepo returnRequestRepo, PaymentRepo paymentRepo,
                            WalletService walletService, WalletRepo walletRepo) {
        this.cartService = cartService;
        this.addressService = addressService;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.orderAddressRepo = orderAddressRepo;
        this.productService = productService;
        this.mailService = mailService;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.returnRequestRepo = returnRequestRepo;
        this.paymentRepo = paymentRepo;
        this.walletService = walletService;
        this.walletRepo = walletRepo;
    }

    @Override
    public List<Order> findOrdersByCustomer(Customer customer) {
        return orderRepo.findByCustomer(customer);
    }

    @Override
    public Order findById(Long id, String email) {
        try{
            Order order = orderRepo.findByIdAndEmail(id, email);
            if(order==null)
                throw new UnableToFindOrderException("Unable to find the order which you're looking for, l-70");
            return order;
        }catch (Exception e){
            if(e instanceof UnableToFindOrderException) throw e;
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    @Override
    public void putProductsBackAndCancelUnlessReturn(Order order, String emailOfCustomer, boolean admin, boolean isReturn) {
        try{
            for(OrderItem orderItem : order.getOrderItems()){
                Product product = orderItem.getProduct();
                if(productService.existsById(product.getId())){
                    productService.setQuantity(product.getId(),
                            product.getQuantity()+orderItem.getQuantityPerItem());
                }
            }
            if(!isReturn) {
                orderRepo.cancelOrder(order.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Order getOrder(String emailOfCustomer, Optional<Order> optionalOrder) {
        Order order;

        if(optionalOrder.isEmpty())
            throw new UnableToFindOrderException("Couldn't find the order which you're trying to cancel.");

        order = optionalOrder.get();

        if(!Objects.equals(order.getCustomer().getEmail(), emailOfCustomer))
            throw new InvalidStateException("", "Couldn't find the order which you're trying to cancel");

        return order;
    }



    @Transactional
    @Override
    public void setOrderStatus(String status, Long id, Order order) {

        try{
            if(status==null)
                throw new InvalidStateException("", "Please try after sometime, l-157");

            if(Objects.equals(order.getOrderStatus(), "DELIVERED") ||
                    Objects.equals(order.getOrderStatus(), "CANCELLED") ||
                    Objects.equals("RETURN", order.getOrderStatus()))
                throw new InvalidStateException("", "Cannot update order as order is past updating stage");

            if(!existsById(id))
                throw new UnableToFindOrderException("Unable to find the order which you're looking for");

            if(Objects.equals(status, "CANCELLED")){
                if(!Objects.equals(order.getPaymentMethod(), "RAZORPAY"))
                    putProductsBackAndCancelUnlessReturn(order, order.getCustomer().getEmail(), true, false);
                mailService.sendMail(order.getCustomer().getEmail(), mailService.orderCancelledMailStructure(order));
                return;
            }
            if(Objects.equals(status, "PROCESSING")){
                orderRepo.setOrderStatus(id, status);
                mailService.sendMail(order.getCustomer().getEmail(), mailService.orderPlacedMailStructure(order));
                return;
            }
            if(Objects.equals(status, "SHIPPED")){
                orderRepo.setOrderStatus(id, status);
                mailService.sendMail(order.getCustomer().getEmail(), mailService.orderShippedMailStructure(order));
                return;
            }
            if(Objects.equals(status, "DELIVERED")){
                orderRepo.setOrderStatus(id, status);
                orderRepo.setDeliveryDateAsToday(id, LocalDate.now());
                if(Objects.equals(order.getPaymentMethod(), "COD")){
                    Payment payment = order.getPayment();
                    payment.setStatus("PAID");
                    paymentRepo.save(payment);
                }

                mailService.sendMail(order.getCustomer().getEmail(), mailService.orderDeliveredMailStructure(order));
                return;
            }
            if(Objects.equals(status, "ACCEPTED")) {
                orderRepo.acceptOrder(id);
                mailService.sendMail(order.getCustomer().getEmail(), mailService.orderAcceptedMailStructure(order));
            }
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException || e instanceof UnableToFindOrderException)
                throw e;
        }

    }

    @Override
    public List<Order> filterOrders(OrderFilterDto orderFilterDto) {
        try{
            if(orderFilterDto==null) return orderRepo.findAll();

            List<String> paymentMethods = Arrays.asList(orderFilterDto.getPaymentMethod());
            List<String> statuses = Arrays.asList(orderFilterDto.getStatus());

            if(orderFilterDto.getPaymentMethod() == null) paymentMethods = Arrays.asList("COD","RAZORPAY","PAYPAL");
            if(orderFilterDto.getStatus() == null) statuses = Arrays.asList("PROCESSING", "ACCEPTED", "DELIVERED", "SHIPPED", "CANCELLED", "RETURN");

            if(Objects.equals(orderFilterDto.getSort(), "NEWEST"))
                return orderRepo.filterOrdersDesc(paymentMethods, statuses);
            else if(Objects.equals(orderFilterDto.getSort(), "OLDEST"))
                return orderRepo.filterOrdersAsc(paymentMethods, statuses);
            else if(orderFilterDto.getSort()==null)
                return orderRepo.filterOrdersAsc(paymentMethods, statuses);

            return orderRepo.findAll();
        }catch (Exception e){
            throw new InvalidStateException("","Couldn't apply filter, try again later!");
        }
    }

    @Transactional
    public void placeOrderCod(Long addressId, String customerEmail, boolean useWalletBalance) {

        try{
            Cart cart = cartService.findByUsername(customerEmail);
            AddressDto address = addressId!=null?addressService.findAddressById(addressId):addressService.findDefaultAddressByCustomerEmail(customerEmail);
            Customer customer = cart.getCustomer();
            if(customer==null)
                throw new InvalidStateException("", "Unable to place order, l-208");

            if(cart.getCartItems().isEmpty())
                throw new InvalidStateException("Less Number of items In cart", "Unable to proceed, Cart doesn't have enough items, l-211");

            if(validateCartItemQuantity(cart))
                throw new UnableToMakeOrderException("", "Lesser number of stock available for the items in your Cart. Update your cart before trying again");


            if(sameReq(customer, cart.getCartItems()))
                throw new SameOrderException("");


            double totalPrice = (double) Math.round(cart.getTotalPrice() * 1.18);

            Order order = new Order();
            order.setOrderDate(LocalDate.now());
            order.setDeliveryDate(LocalDate.now().plusDays(7));
            order.setIsAccepted(false);
            order.setIsCancelled(false);
            order.setOrderStatus("PROCESSING");
            order.setPaymentMethod("COD");
            order.setQuantity(cart.getCartItems().size());
            order.setTax((double) Math.round(totalPrice - cart.getTotalPrice()));
            order.setTotalPrice(totalPrice);
            order.setPayment(new Payment(null, null, order.getTotalPrice(), null, "PENDING", null, order, false,"NEVER", 0.0));

            /////
            if(useWalletBalance){
                Wallet wallet = walletService.getWallet(customer);
                if(wallet.getBalance()>0.0){
                    if(wallet.getBalance()>=order.getTotalPrice()){
                        order.getPayment().setDeductedFromWallet(totalPrice);
                        wallet.setBalance(wallet.getBalance()-totalPrice);
                    }else{
                        order.getPayment().setDeductedFromWallet(wallet.getBalance());
                        wallet.setBalance(0.0);
                    }
                }
                order.getPayment().setAmount((totalPrice-order.getPayment().getDeductedFromWallet()));
            }else order.getPayment().setAmount(totalPrice);
            ///////

            if(cart.getCoupon()!=null){
                List<Coupon> customerUsedCoupons = customer.getCoupons();
                if(customerUsedCoupons==null){
                    customerUsedCoupons=new ArrayList<>();
                }
                customerUsedCoupons.add(cart.getCoupon());
                order.setCoupon(cart.getCoupon());
                cart.setCoupon(null);
            }
            order.setCustomer(customer);

            List<OrderItem> orderItems = cartItemsToOrderItems(cart.getCartItems(), order);

            order.setOrderItems(orderItems);
            OrderAddress orderAddress = addressService.addressDtoToOrderAddress(address, order);
            orderAddress.setOrder(orderRepo.save(order));
            order.setOrderAddress(orderAddressRepo.save(orderAddress));
            orderRepo.save(order);

            cartItemRepo.deleteByCartId(cart.getId());
            cart.setTotalItems(0);
            cart.setTotalPrice(0.0);
            cartRepo.save(cart);

            mailService.sendMail(customerEmail, mailService.orderPlacedMailStructure(order));

        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException || e instanceof UnableToMakeOrderException)
                throw e;
            throw new UnableToMakeOrderException("", "Unable to place order, sorry for the inconvenience caused!");
        }
    }


    public boolean validateCartItemQuantity(Cart cart){
        for(CartItem cartItem: cart.getCartItems()){
            Product product = cartItem.getProduct();
            if(cartItem.getQuantity()>cartItem.getProduct().getQuantity() || cartItem.getProduct().isDeleted()){
                throw new UnableToMakeOrderException("", "Item "+cartItem.getProduct().getName()+" either has less number of stock available or is removed. Update your cart before trying again");
            }
            product.setQuantity(product.getQuantity()-cartItem.getQuantity());
        }
        return false;
    }



    private boolean sameReq(Customer customer, Set<CartItem> cartItems) {
//        List<Order> orders = orderRepo.findAllProcessingForCustomer(customer);
//        for(Order order : orders)
//            for(OrderItem orderItem : order.getOrderItems())
//                if(orderItem.getProduct()==)

        //handle the case when multiple clicks are made at once multiple orders shouldn't be placed
        return false;
    }

    @Override
    public String getOrderStatus(Long id) {
        try{return orderRepo.getOrderStatus(id);}
        catch (Exception e){throw new InvalidStateException("","Couldn't fetch current order status, try after sometime.");}
    }


    public List<OrderItem> cartItemsToOrderItems(Set<CartItem> cartItems, Order order) {
        if(cartItems==null || cartItems.isEmpty())
            throw new InvalidStateException("", "Cart cannot be empty while placing an order");
        return cartItems.stream()
                .map(cartItem -> new OrderItem(null, cartItem.getProduct(), order, cartItem.getSize(),  cartItem.getQuantity(), cartItem.getTotalPrice())
                ).collect(Collectors.toList());
    }

    @Override
    public void updateOrderAddressWhenOrderProcessing(Long id, AddressDto addressDto, Order order) {
        if(order.getIsCancelled())
            throw new InvalidStateException("","Cannot update address as order is already past processing stage");
        orderAddressRepo.save(addressService.addressDtoToOrderAddress(addressDto, order));
    }

    @Override
    public List<Order> findAll() {
        try{return orderRepo.findAll();}
        catch(Exception e){
            e.printStackTrace();
            throw new UnableToFindOrderException("Unable to fetch orders, l-121");
        }
    }

    @Override
    public Order findById(Long id) {
        try{
            Optional<Order> optionalOrder = orderRepo.findById(id);
            if(optionalOrder.isPresent())
                return optionalOrder.get();
            throw new UnableToFindOrderException("Couldn't find the order you're looking for, l-130");
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof UnableToFindOrderException) throw e;
        }
        return null;
    }

    @Override
    public boolean isOrderCancelled(Long id) {
        try{
            if(orderRepo.existsById(id))
                return Objects.equals(orderRepo.findById(id).get().getOrderStatus(), "CANCELLED");
            throw new UnableToFindOrderException("Order doesn't exist, l-144");
        }catch (Exception e){
            if (e instanceof UnableToFindOrderException) throw e;
            throw new InvalidStateException("","Couldn't fetch Order details");
        }
    }

    @Override
    public boolean existsById(Long id) {
        try{
            return orderRepo.existsById(id);
        }catch (Exception e){
            throw new InvalidStateException("","Couldn't fetch Order details");
        }
    }

    @Override
    public String getOrderPaymentMethod(Long id) {
        try{
            return orderRepo.getPaymentMethodById(id);
        }catch (Exception e){
            throw new InvalidStateException("","Couldn't fetch Order details");
        }
    }

    @Transactional
    @Override
    public void returnOrder(Order order) {
        try{
            LocalDate maxDate = order.getDeliveryDate().plusDays(7);


            if(!Objects.equals(order.getOrderStatus(), "DELIVERED") || LocalDate.now().isAfter(maxDate)){
                throw new OrderPastProcessingException("","Cannot Accept return request as Order may past Return date!");
            }
            order.setReturnRequest(returnRequestRepo.save(new ReturnRequest(null, true, false,
                    new Date(), null, order.getPaymentMethod(),
                    (order.getTotalPrice()-order.getTax()), order)));

            order.setOrderStatus("RETURN");
            orderRepo.save(order);
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof OrderPastProcessingException) throw e;
            throw new UnableToInitiateReturn("Couldn't Initiate Return, contact customer care for more details");
        }
    }

    @Transactional
    @Override
    public void initiateRefundForCodReturn(Long orderId) {

        try{
            Optional<Order> optionalOrder = orderRepo.findById(orderId);
            if(optionalOrder.isEmpty())
                throw new InvalidStateException("","Cannot initiate refund as order doesn't exist");

            Order order = optionalOrder.get();

            if(!Objects.equals(order.getPayment().getStatus(), "PAID"))
                throw new InvalidStateException("","Cannot initiate refund as payment was not done yet!");

            ReturnRequest returnRequest = order.getReturnRequest();
            Payment payment = order.getPayment();

            returnRequest.setReturnAccepted(true);
            returnRequest.setReturnedDate(new Date());

            payment.setRefundInitiated(true);
            payment.setRefundStatus("REFUND INITIATED");

            putProductsBackAndCancelUnlessReturn(order, order.getCustomer().getEmail(), true, true);


            walletService.initiateWalletRefund(order, order.getTotalPrice()-order.getTax());
            paymentRepo.save(payment);
            returnRequestRepo.save(returnRequest);
        }catch (Exception e){
            if(e instanceof InvalidStateException) throw e;
            throw new UnableToInitiateRefund("Couldn't initiate refund, contact customer care for more details");
        }

    }

    @Override
    public Double findAmountFromDeliveredOrdersPastReturn() {
        try{
            Double amount = orderRepo.findAmountFromDeliveredOrdersPastReturn(LocalDate.now().minusDays(7));
            return amount!=null?amount:0.0;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Order> findOrdersByStatus(String orderStatus) {
        try{
            return orderRepo.findOrderByOrderStatus(orderStatus);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, order-s l-479");
        }

    }

    @Transactional
    @Override
    public void cancelOrderCod(Long id, String email) {
        Optional<Order> optionalOrder = orderRepo.findById(id);
        Order order = getOrder(email, optionalOrder);
        if(order.getPayment().getDeductedFromWallet()>0.0){
            walletService.initiateWalletRefund(order, order.getPayment().getDeductedFromWallet());
        }
        putProductsBackAndCancelUnlessReturn(order, email, false, false);
    }


}
