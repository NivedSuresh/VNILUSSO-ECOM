package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Dtos.CouponDto;
import com.ecommerce.admin.LIBRARY.ProductsService.CouponService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/coupons")
public class CouponController {
    CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping
    public String viewAllCoupons(Model model, HttpSession session){
        BindingResult result = (BindingResult) session.getAttribute("error");
        if(result!=null){
            model.addAttribute("addCoupon", result);
            session.removeAttribute("error");
        }
        model.addAttribute("coupons", couponService.findAll());
        return "/PostAuth/coupons";
    }

    @PostMapping("/add")
    public String addCoupon(@Valid CouponDto coupon, BindingResult result,
                            HttpSession session){
        if(result.hasErrors()) session.setAttribute("error", result);
        else couponService.save(couponService.dtoToEntity(coupon, null));
        return "redirect:/coupons";
    }

    @GetMapping("/edit/{id}")
    public String editCoupon(@PathVariable("id") Long id, Model model,HttpSession session){
        BindingResult result = (BindingResult) session.getAttribute("addCoupon");
        if(result!=null) {
            model.addAttribute("addCoupon", result);
            session.removeAttribute("addCoupon");
        }
        model.addAttribute("coupon",couponService.entityToDto(couponService.findById(id)));
        return "/PostAuth/edit-coupon";
    }

    @PostMapping("/update/{id}")
    public String updateCoupon(@PathVariable Long id,@Valid CouponDto couponDto, BindingResult result,
                               HttpSession session){
        if(result.hasErrors()){
            session.setAttribute("addCoupon", result);
            return "redirect:/coupons/edit/"+id+"?error";
        }
        else couponService.save(couponService.dtoToEntity(couponDto, id));
        return "redirect:/coupons/edit/"+id+"?success";
    }
}
