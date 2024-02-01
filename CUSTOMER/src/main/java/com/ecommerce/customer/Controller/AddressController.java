package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Dtos.AddressDto;
import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.ProductsService.AddressService;
import com.ecommerce.customer.LIBRARY.ProductsService.OrderService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/address")
public class AddressController {

    AddressService addressService;
    CustomerService customerService;
    OrderService orderService;

    @Autowired
    public AddressController(AddressService addressService, CustomerService customerService,
                             OrderService orderService) {
        this.addressService = addressService;
        this.customerService = customerService;
        this.orderService = orderService;
    }


    @GetMapping("/{id}")
    public String editOrViewAddress(@PathVariable Long id, Model model){
        if(!addressService.existsById(id)){
            return "redirect:/checkout?invalid-address";
        }
        model.addAttribute("addressDto", addressService.findAddressById(id));
        return "/PostAuth/editAddress";
    }

    @PostMapping("/update/{id}")
    public String updateAddress(@PathVariable("id") Long id, AddressDto addressDto,
                                Principal principal){
        if(addressService.addressBelongsToCustomer(id, principal.getName())){
            addressDto.setId(id);
            addressService.saveAddress(principal.getName(), addressDto);
            return "redirect:/address/"+id;
        }
        return "redirect:/checkout?invalid-address";
    }

    @PostMapping("/update/order-address/{id}")
    public String updateOrderAddressWhenOrderProcessing(@PathVariable("id") Long id,
                                                        @RequestParam("orderId") Long orderId,
                                                        @Valid AddressDto addressDto,
                                                        BindingResult bindingResult, Model model){
        Order order = orderService.findById(orderId);
        if(bindingResult.hasErrors()){
            model.addAttribute("result", bindingResult);
            model.addAttribute("order", order);
            model.addAttribute("orderAddress", order.getOrderAddress());
            return "PostAuth/orderDetails";
        }
        orderService.updateOrderAddressWhenOrderProcessing(id, addressDto, order);
        return "redirect:/order/details/"+orderId;

    }



}
