package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Dtos.OrderFilterDto;
import com.ecommerce.admin.LIBRARY.Model.User.Order;
import com.ecommerce.admin.LIBRARY.ProductsService.CategoryService;
import com.ecommerce.admin.LIBRARY.ProductsService.OrderService;
import com.ecommerce.admin.LIBRARY.ProductsService.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@RequestMapping("/dashboard")
@Controller
public class DashboardController {


    OrderService orderService;
    CategoryService categoryService;
    StatisticsService statisticsService;

    public DashboardController(OrderService orderService, CategoryService categoryService,
                               StatisticsService statisticsService) {
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.statisticsService = statisticsService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder webDataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping
    public String dashboard(@ModelAttribute("orders") ArrayList<Order> orders, Model model,
                            @RequestParam(required = false , value = "timeInterval") String timeInterval){

        if(orders==null || orders.isEmpty())model.addAttribute("orders", orderService.findAll());
        else model.addAttribute("orders", orders);

        Map<String, Double> sales = null;
        if(timeInterval == null || timeInterval.equals("MONTHLY")) sales = statisticsService.salesPerMonth();
        else if(timeInterval.equals("YEARLY")) sales = statisticsService.salesPerYear();
        else sales = statisticsService.salesPerDay(LocalDate.now());

        model.addAttribute("keys", sales.keySet());
        model.addAttribute("sales", sales.values());
        model.addAttribute("statistics", statisticsService.getDashBoardStatistics());
        model.addAttribute("categories", categoryService.findAllActiveCategories());

        return "PostAuth/dashboard";
    }

    @PostMapping("/orders/filter")
    public String filterOrders(OrderFilterDto filterDto, RedirectAttributes attributes){
        attributes.addAttribute("orders",orderService.filterOrders(filterDto));
        return "redirect:/dashboard";
    }

}
