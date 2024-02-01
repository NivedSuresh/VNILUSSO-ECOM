package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Dtos.CustomerDto;
import com.ecommerce.customer.LIBRARY.Model.Utils.Token;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import com.ecommerce.customer.LIBRARY.Service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class LoginController {

    CsrfTokenRepository csrfTokenRepository;
    CustomerService customerService;
    TokenService tokenService;

    public LoginController(CsrfTokenRepository csrfTokenRepository, CustomerService customerService,
                           TokenService tokenService) {
        this.customerService = customerService;
        this.csrfTokenRepository = csrfTokenRepository;
        this.tokenService = tokenService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder webDataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model, Principal principal){
        if(principal!=null)
            return "redirect:/";
        request.getSession(true);
        model.addAttribute("customer",new CustomerDto(null));
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        String token = csrfToken.getToken();
        request.getSession().setAttribute("_csrf", token);
        return "/login-register";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @Email @RequestParam(value = "email" , required = false) String email){
        if(email!=null && customerService.existsByEmail(email)){
            customerService.generateTokenForResetPassword(email, true);
            return "redirect:/login?token_sent";
        }
        return "forgot-password";
    }

    @PostMapping("/forgot-password/send-token")
    public String sendTokenForForgotPassword(String email) {
        if (email != null && customerService.existsByEmail(email)) {
            customerService.generateTokenForResetPassword(email, true);
            return "redirect:/checkout?token_sent";
        }
        return "redirect:/forgot-password?invalid";
    }

    @GetMapping("/reset-password")
    public String enterNewPassword(@RequestParam("token") String token, @RequestParam("email") String email,
                                   Model model){
        if(email!=null && token!=null && tokenService.validateToken(tokenService.findByToken(token), email)){
            model.addAttribute("email", email);
            return "/reset-password";
        }
        return null;
    }

    @PostMapping("reset-password")
    public String saveChangesForgotPassword(String email, String password, String confirmPassword){
        Token token = tokenService.findByUsernameAndTokenFor(email, "PASSWORD_RESET");
        tokenService.validateToken(token);

        if(password==null || !password.equals(confirmPassword) || password.length()<3)
            return "redirect:/reset-password?token="+token.getToken()+"&email="+email+"&error";

        customerService.resetPasswordValidateToken(email, token.getToken(),  password);
        return "redirect:/login";
    }


}
