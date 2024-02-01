package com.ecommerce.customer.LIBRARY.ProductsService;

import com.ecommerce.customer.LIBRARY.Dtos.DashboardStatistics;
import com.ecommerce.customer.LIBRARY.Dtos.StatisticsDto;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {

    DashboardStatistics getDashBoardStatistics();
    Map<String, Double> salesPerMonth();
    Integer totalDeliveredOrders();
    Double getRevenue();
    Integer getOrdersExcludingTransit();
    Integer totalActiveProducts();
    Integer totalActiveCategories();
    Map<String, Double> getSalesPerCategory();
    StatisticsDto getAllStatistics();
    Map<String, Double> salesPerYear();
    Map<String, Double> salesPerDay(LocalDate date);
    Map<String, Integer> getOrderStatusCount();
}
