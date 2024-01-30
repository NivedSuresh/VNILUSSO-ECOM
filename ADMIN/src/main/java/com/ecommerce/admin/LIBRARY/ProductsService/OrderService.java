package com.ecommerce.admin.LIBRARY.ProductsService;

import com.ecommerce.admin.LIBRARY.Dtos.AddressDto;
import com.ecommerce.admin.LIBRARY.Dtos.OrderFilterDto;
import com.ecommerce.admin.LIBRARY.Model.User.*;

import java.util.List;
import java.util.Set;

public interface OrderService {
    void placeOrderCod(Long addressId, String customerEmail, boolean useWalletBalance);

    List<Order> findOrdersByCustomer(Customer byEmail);

    Order findById(Long id, String email);

    void putProductsBackAndCancelUnlessReturn(Order order, String emailOfCustomer, boolean admin, boolean isReturn);

    List<Order> findAll();

    Order findById(Long id);

    boolean isOrderCancelled(Long id);

    boolean existsById(Long id);

    void setOrderStatus(String upperCase, Long id, Order order);

    void updateOrderAddressWhenOrderProcessing(Long id, AddressDto addressDto, Order order);

    String getOrderStatus(Long id);

    List<Order> filterOrders(OrderFilterDto orderFilterDto);

    List<OrderItem> cartItemsToOrderItems(Set<CartItem> cartItems, Order order);

    boolean validateCartItemQuantity(Cart cart);

    String getOrderPaymentMethod(Long id);

    void returnOrder(Order byId);

    void initiateRefundForCodReturn(Long orderId);

    Double findAmountFromDeliveredOrdersPastReturn();

    List<Order> findOrdersByStatus(String orderStatus);

    void cancelOrderCod(Long id, String email);
}
