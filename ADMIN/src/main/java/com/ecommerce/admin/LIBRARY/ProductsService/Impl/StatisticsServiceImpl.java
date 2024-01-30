package com.ecommerce.admin.LIBRARY.ProductsService.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.DashboardStatistics;
import com.ecommerce.admin.LIBRARY.Dtos.StatisticsDto;
import com.ecommerce.admin.LIBRARY.Model.User.Category;
import com.ecommerce.admin.LIBRARY.Model.User.Product;
import com.ecommerce.admin.LIBRARY.ProductsService.StatisticsService;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    OrderRepo orderRepo;
    ProductRepo productRepo;
    PaymentRepo paymentRepo;
    CategoryRepo categoryRepo;
    OrderItemRepo orderItemRepo;

    public StatisticsServiceImpl(OrderRepo orderRepo, PaymentRepo paymentRepo, ProductRepo productRepo,
                                 CategoryRepo categoryRepo, OrderItemRepo orderItemRepo) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public StatisticsDto getAllStatistics(){
        StatisticsDto statisticsDto = new StatisticsDto();
        LocalDate now = LocalDate.now();
        statisticsDto.setMonthlySales(getMonthlySales(now));
        statisticsDto.setTotalCategories(totalActiveCategories());
        statisticsDto.setSalesPerCategory(getSalesPerCategory());
        statisticsDto.setTotalProducts(totalActiveProducts());
        statisticsDto.setTotalOrders(totalDeliveredOrders());
        statisticsDto.setMostSoldProducts(getMostSoldProducts());
        statisticsDto.setDailySales(getSales(now, now));
        statisticsDto.setWeeklySales(getSales(now.minusWeeks(1), now));
        statisticsDto.setYearlySales(getSales(now.minusMonths(now.getMonth().getValue()-1).minusDays(now.getDayOfMonth()-1),
                now.plusMonths(12-now.getMonth().getValue())));
        statisticsDto.setOrderStatusCount(getOrderStatusCount());
        return statisticsDto;
    }

    public Map<String, Integer> getOrderStatusCount() {
        Map<String, Integer> orderStatusCount = new HashMap<>();
        String [] orderStatus = {"PROCESSING", "ACCEPTED", "CANCELLED", "RETURN", "DELIVERED", "SHIPPED"};
        for(String status : orderStatus){
            orderStatusCount.put(status, orderRepo.totalOrderForStatus(status));
        }
        return orderStatusCount;
    }

    @Override
    public Map<String, Double> salesPerYear() {
        Map<String, Double> salesPerYear = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        LocalDate minYearAndDate = null;
        try{minYearAndDate = orderRepo.getFirstOrderYear();}
        catch (Exception e){minYearAndDate = now.minusYears(2);}

        for(int i=now.getYear()-minYearAndDate.getYear() ; i>=0 ; i--){
            LocalDate firstDay = now.minusYears(i).minusMonths(now.getMonth().getValue()-1);
            LocalDate lastDay = now.minusYears(i).plusMonths(12- now.getMonthValue()).plusDays(30);
            salesPerYear.put(String.valueOf(firstDay.getYear()), getSales(firstDay, lastDay));
        }

        return salesPerYear;
    }

    @Override
    public Map<String, Double> salesPerDay(LocalDate now) {
        Map<String, Double> salesPerDay = new LinkedHashMap<>();
        now = now.minusDays(now.getDayOfMonth()-1);
        for(int i=0 ; i<now.lengthOfMonth() ; i++){
            LocalDate day = now.plusDays(i);
            Double sale = getSales(day, day);
            salesPerDay.put(String.valueOf(i+1), sale);
        }
        return salesPerDay;
    }

    public Double getMonthlySales(LocalDate now){
        return getSales(LocalDate.of(now.getYear(), now.getMonth(), 1),
                LocalDate.of(now.getYear(), now.getMonth(),
                        getLastDayOfMonth(now.getMonth().getValue(), now.getYear())) );
    }



    @Override
    public DashboardStatistics getDashBoardStatistics() {
        LocalDate now = LocalDate.now();
        DashboardStatistics dashboardStatistics = new DashboardStatistics();
        dashboardStatistics.setRevenue(getRevenue());
        dashboardStatistics.setTotalProducts(totalActiveProducts());
        dashboardStatistics.setTotalCategories(totalActiveCategories());
        dashboardStatistics.setOrdersExcludingTransit(getOrdersExcludingTransit());
        dashboardStatistics.setMonthlySales(
                getSales(LocalDate.of(now.getYear(), now.getMonth(), 1), LocalDate.of(now.getYear(), now.getMonth(), getLastDayOfMonth(now.getMonth().getValue(), now.getYear())) )
        );
        return dashboardStatistics;
    }

    @Override
    public Map<String, Double> salesPerMonth() {
        Map<String, Double> salesPerMonth = new LinkedHashMap<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int currentYear = LocalDate.now().getYear();
        for(int i=0 ; i<12 ; i++){
            int lastDay = getLastDayOfMonth(i+1, currentYear);
            salesPerMonth.put(months[i], getSales(LocalDate.of(currentYear, i+1, 1),
                    LocalDate.of(currentYear, i+1, lastDay)
                    ));
        }
        return salesPerMonth;
    }

    public int getLastDayOfMonth(int month, int currentYear){
        if(month!=2 && month<=7)return  (month)%2==0?30:31;
        else if(month>7)return (month)%2==0?31:30;
        else return (currentYear % 4 == 0 && currentYear % 100 != 0) ||
                    (currentYear % 400 == 0)?29:28;
    }


    private Double getSales(LocalDate firstDay, LocalDate lastDay) {
        Double sales = null;
        try {sales = orderRepo.getSales(firstDay, lastDay);}
        catch (Exception e){e.printStackTrace();}
        return sales==null?0.0:sales;
    }

    public Integer totalDeliveredOrders() {
        Integer totalDeliveredOrders = null;
        try{totalDeliveredOrders = orderRepo.getTotalDeliveredOrders();}
        catch (Exception e){e.printStackTrace();}
        return totalDeliveredOrders==null?0:totalDeliveredOrders;
    }
    public Double getRevenue(){
        Double revenue = null;
        try{revenue = orderRepo.getSales(orderRepo.getFirstOrderYear(), LocalDate.now());}
        catch (Exception e){e.printStackTrace();}
        return revenue==null?0.0:Math.round(revenue);
    }
    public Integer getOrdersExcludingTransit(){
        Integer ordersExcludingTransit = null;
        try{ordersExcludingTransit = orderRepo.getOrdersExcludingTransit();}
        catch (Exception e){e.printStackTrace();}
        return ordersExcludingTransit==null?0:ordersExcludingTransit;
    }
    public Integer totalActiveProducts(){
        Integer totalActiveProducts = null;
        try{
            totalActiveProducts = productRepo.getActiveProductsCount();
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalActiveProducts==null?0:totalActiveProducts;
    }
    public Integer totalActiveCategories(){
        Integer totalActiveCategories = null;
        try{
            totalActiveCategories = categoryRepo.getActiveCategoriesCount();
        }catch (Exception e){
            e.printStackTrace();
        }
        return totalActiveCategories==null?0:totalActiveCategories;
    }
    public List<StatisticsDto.MostSoldProducts> getMostSoldProducts() {
        try{
            List<Object[]> results = orderItemRepo.mostSoldProducts();
            List<StatisticsDto.MostSoldProducts> mostSoldProducts = new ArrayList<>();
            for (Object[] result : results) {
                mostSoldProducts.add(new StatisticsDto.MostSoldProducts((Product) result[0], (Long) result[1],
                        (Double) result[2]));
            }
            return mostSoldProducts;
        }catch (Exception e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public Map<String, Double> getSalesPerCategory(){
        List<Object[]> results = null;
        try{results = orderItemRepo.getSalesPerCategory();}
        catch (Exception e){
            results = Collections.emptyList();
            e.printStackTrace();
        }
        Map<String, Double> salesPerCategory = new LinkedHashMap<>();
        for (Object[] result : results) {
            salesPerCategory.put(((Category) result[0]).getCategory(), (Double) result[1]);
        }
        return salesPerCategory;
    }

}
