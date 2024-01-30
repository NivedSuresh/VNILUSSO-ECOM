package com.ecommerce.admin.LIBRARY.Repository.UserRepos;

import com.ecommerce.admin.LIBRARY.Model.User.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    @Query(value = "select o.product, sum(o.quantityPerItem), sum(o.quantityPerItem*o.product.salePrice) as c from OrderItem as o where o.order.orderStatus = 'DELIVERED' group by o.product order by sum(o.quantityPerItem) desc ")
    List<Object[]> mostSoldProducts();

    @Query("SELECT o.product.category, SUM(o.product.salePrice * o.quantityPerItem) as sum FROM OrderItem o where o.order.orderStatus = 'DELIVERED' GROUP BY o.product.category order by sum desc")
    List<Object[]> getSalesPerCategory();

}
