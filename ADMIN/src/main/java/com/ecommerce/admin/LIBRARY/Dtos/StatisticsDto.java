package com.ecommerce.admin.LIBRARY.Dtos;

import com.ecommerce.admin.LIBRARY.Model.User.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class StatisticsDto {
    private Integer totalProducts;
    private Integer totalCategories;
    private Integer totalOrders;

    private Double dailySales;
    private Double weeklySales;
    private Double monthlySales;
    private Double yearlySales;

    private Map<String, Double> salesPerCategory;

    private List<MostSoldProducts> mostSoldProducts;

    private Map<String, Integer> orderStatusCount;

    @Setter @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MostSoldProducts {
        Product product;
        Long count;
        Double sales;
    }

}
