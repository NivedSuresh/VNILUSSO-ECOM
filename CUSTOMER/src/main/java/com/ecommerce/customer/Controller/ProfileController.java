package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Dtos.CustomerDto;
import com.ecommerce.customer.LIBRARY.ProductsService.AddressService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import com.ecommerce.customer.LIBRARY.Service.OtpService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@RequestMapping("/profile")
@Controller
public class ProfileController {

    AddressService addressService;
    OtpService otpService;
    CustomerService customerService;

    public ProfileController(AddressService addressService, CustomerService customerService,
                             OtpService otpService) {
        this.addressService = addressService;
        this.customerService = customerService;
        this.otpService = otpService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder webDataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, ste);
    }


    @GetMapping
    public String getProfile(Model model, Principal principal){

        model.addAttribute("addresses", addressService.findAddressByCustomer(principal.getName()));
        model.addAttribute("customer", customerService.getCustomerDto(principal.getName()));
        return "/PostAuth/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid CustomerDto customerDto, BindingResult result,
                                HttpSession session, Principal principal, Model model){

        if(result.hasFieldErrors("email")||result.hasFieldErrors("phoneNumber")||
                result.hasFieldErrors("username")){
            return "redirect:/profile?error";
        }
        //will return true if email doesn't have to be updated
        if(customerService.updateProfile(customerDto.getEmail(), customerDto.getUsername(), customerDto.getPhoneNumber(), principal, session))
            return "redirect:/profile";
        else{
            //finish updating email
            return "redirect:/profile/update/enter-otp";
        }
    }

    @GetMapping("/update/enter-otp")
    public String enterOtpView(){
        return "/PostAuth/otp-entry";
    }

    @PostMapping("/update/verify-otp")
    public String verifyOtpAndUpdateEmail(String otp, HttpSession session, Principal principal){
        String email = (String) session.getAttribute("email");
        if(otpService.validateOtp(otp, principal.getName()) && email!=null && !customerService.existsByEmail(email)){
            customerService.finishUpdateEmail(email, principal.getName());

            //after changing email update your UsernamePasswordAuthenticationToken with the new email
            //so when you're redirecting to profile it will be able to fetch data based on the new email
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/profile";
        }
        return "redirect:/profile/update/enter-otp";
    }

    @GetMapping("/change-password")
    public String changePasswordCustomer(Principal principal){
        customerService.generateTokenForResetPassword(principal.getName(), false);
        return "redirect:/profile?sent";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model){
        model.addAttribute("token", token);
        return "/PostAuth/reset-password";
    }


    @PostMapping("/reset-password")
    public String resetPassword(String token, String password, String confirmPassword, Model model, Principal principal){
        if(password==null || password.length()<3 || !Objects.equals(password, confirmPassword))
            return "redirect:/profile/reset-password?error";
        customerService.resetPasswordValidateToken(principal.getName(), token,  password);
        return "redirect:/profile?success";
    }


}
