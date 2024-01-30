package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Dtos.StatisticsDto;
import com.ecommerce.admin.LIBRARY.ProductsService.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticsController {

    StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public String getStatistics(Model model){
        StatisticsDto statisticsDto = statisticsService.getAllStatistics();
        model.addAttribute("statistics", statisticsDto);
        return "/PostAuth/statistics";
    }

}
