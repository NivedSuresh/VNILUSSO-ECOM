package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import com.ecommerce.customer.LIBRARY.Model.User.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(Customer customer);

    @Query("select o from Order o where o.id = :id and o.customer.email = :email")
    Order findByIdAndEmail(Long id, String email);

    @Modifying @Transactional
    @Query("update Order  o set o.isCancelled=true, o.orderStatus = 'CANCELLED'  where o.id = :id")
    void cancelOrder(Long id);

    @Modifying
    @Query("update Order o set o.orderStatus = :status where o.id = :id")
    @Transactional
    void setOrderStatus(@Param("id") Long id, @Param("status") String status);

    @Modifying @Transactional
    @Query("update Order o set o.isAccepted = true, o.orderStatus='ACCEPTED' where o.id = :id")
    void acceptOrder(Long id);

    @Query("select o from Order o where o.customer = :customer and o.orderStatus='PROCESSING'")
    List<Order> findAllProcessingForCustomer(Customer customer);

    @Query("select o.orderStatus from Order o where o.id = :id")
    String getOrderStatus(Long id);

    @Query("select o from Order o where o.paymentMethod in (:paymentMethods) and o.orderStatus in (:statuses) order by o.orderDate desc")
    List<Order> filterOrdersDesc(List<String> paymentMethods, List<String> statuses);


    @Query("select o from Order o where o.paymentMethod in (:paymentMethods) and o.orderStatus in (:statuses) order by o.orderDate asc")
    List<Order> filterOrdersAsc(List<String> paymentMethods, List<String> statuses);

    @Query("select o.paymentMethod from Order  o where o.id = :id")
    String getPaymentMethodById(Long id);

    @Query("select count(o) from Order o where o.orderStatus = 'PROCESSING' OR o.orderStatus = 'ACCEPTED'")
    Integer getOrdersExcludingTransit();

    @Query("select count(o) from Order o where o.orderStatus = 'DELIVERED'")
    Integer getTotalDeliveredOrders();

    @Modifying @Transactional
    @Query("update Order o set o.deliveryDate = :date where o.id = :id")
    void setDeliveryDateAsToday(Long id, LocalDate date);

    @Query(nativeQuery = true, value = "SELECT SUM(total_price - tax) FROM orders WHERE delivery_date >= :day1 and delivery_date <= :lastDay and order_status = 'DELIVERED'")
    Double getSales(@Param("day1") LocalDate day1, @Param("lastDay") LocalDate lastDay);

    @Query("select min(o.deliveryDate) from  Order o")
    LocalDate getFirstOrderYear();

    @Query("select count(o) from Order o where o.orderStatus = :status")
    Integer totalOrderForStatus(String status);

    @Query("select sum(o.totalPrice-o.tax) from Order o where o.orderStatus = 'DELIVERED' and o.deliveryDate< :date")
    Double findAmountFromDeliveredOrdersPastReturn(LocalDate date);
    List<Order> findOrderByOrderStatus(String orderStatus);
}
