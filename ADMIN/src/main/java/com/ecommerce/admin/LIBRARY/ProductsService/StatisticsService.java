package com.ecommerce.admin.LIBRARY.ProductsService;

import com.ecommerce.admin.LIBRARY.Dtos.DashboardStatistics;
import com.ecommerce.admin.LIBRARY.Dtos.StatisticsDto;

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
