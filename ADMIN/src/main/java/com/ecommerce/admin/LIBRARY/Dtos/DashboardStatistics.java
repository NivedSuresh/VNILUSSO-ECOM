package com.ecommerce.admin.LIBRARY.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardStatistics {
    Integer ordersExcludingTransit;
    Double monthlySales;
    Double revenue;
    Integer totalProducts;
    Integer totalCategories;
}
