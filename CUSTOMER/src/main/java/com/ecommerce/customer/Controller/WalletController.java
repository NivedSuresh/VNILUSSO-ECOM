package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.ProductsService.TaskOfferService;
import com.ecommerce.customer.LIBRARY.ProductsService.WalletService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    WalletService walletService;
    CustomerService customerService;
    TaskOfferService taskOfferService;

    public WalletController(WalletService walletService, CustomerService customerService,
                            TaskOfferService taskOfferService) {
        this.walletService = walletService;
        this.customerService = customerService;
        this.taskOfferService = taskOfferService;
    }

    @GetMapping
    public String getWallet(Model model, Principal principal){
        model.addAttribute("wallet", walletService.getWallet(principal.getName()));
        return "/PostAuth/wallet";
    }

    @PostMapping("/refer")
    public String referUser(String referEmail, Principal principal){
        customerService.referUser(principal.getName(), referEmail,
                taskOfferService.isOfferEnabled("REFERRAL"));
        return "redirect:/wallet";
    }




}
