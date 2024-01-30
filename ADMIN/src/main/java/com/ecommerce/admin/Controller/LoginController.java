package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Model.Utils.Token;
import com.ecommerce.admin.LIBRARY.Repository.UtilRepos.OtpRepo;
import com.ecommerce.admin.LIBRARY.Service.AdminService;
import com.ecommerce.admin.LIBRARY.Service.OtpService;
import com.ecommerce.admin.LIBRARY.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Objects;

@Controller
public class LoginController {

    OtpRepo otpRepo;
    OtpService otpService;

    AdminService adminService;

    TokenService tokenService;

    @Autowired
    public LoginController(OtpRepo otpRepo, OtpService otpService,
                           AdminService adminService,
                           TokenService tokenService) {
        this.otpRepo = otpRepo;
        this.otpService = otpService;
        this.adminService = adminService;
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public String loginPage(Principal principal, Model model, @RequestParam(value = "username", required = false) String username){

        if(principal!=null && adminService.adminExists(principal.getName()))
            return "redirect:/dashboard";

        if(username==null) username = "";
        if(model.containsAttribute("info")) return "loginView";

        model.addAttribute("info", username);
        model.addAttribute("error",null);
        return "loginView";
    }

    @PostMapping("/send-otp")
    public String verifyEmail(Principal principal, @RequestParam(value = "username", required = false)
                                  String username, Model model){
        model.addAttribute("username",username);
        if(adminService.adminExists(username)){
            otpService.generateOtp(username);
            model.addAttribute("info", "Otp sent to "+username);
            return loginPage(principal, model, username);
        }
        return "redirect:/login?invalid";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("username") String username, Model model,
                                 Principal principal){
        if(username!=null && adminService.adminExists(username)){
            model.addAttribute("username", username);
            return sendTokenForgotPassword(principal, username, model);
        }
        return "/forgot-password";
    }

    @PostMapping("/send-token")
    public String sendTokenForgotPassword(Principal principal, @RequestParam("username") String username, Model model){
        if(username!=null && adminService.adminExists(username)){
            tokenService.generateTokenForPasswordReset(username, true);
            model.addAttribute("username", username);
            model.addAttribute("info", "Token to reset password has been sent to "+username);
            return loginPage(principal, model, "");
        }
        return "redirect:/forgot-password?invalid-user";
    }


    @GetMapping("/reset-password")
    public String newPasswordPage(@RequestParam("token") String tokenCode, Model model){
        Token serverToken = tokenService.findByToken(tokenCode);
        if(tokenService.validateToken(serverToken)){
            model.addAttribute("token", tokenCode);
            return "/reset-password";
        }
        return "redirect:/login?error";
    }

    @Transactional
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                @RequestParam("token") String token,
                                Model model, Principal principal){

        if (!Objects.equals(password, confirmPassword)){
            model.addAttribute("password-unmatch", "Passwords don't match");
            return newPasswordPage(token, model);
        }
        String username = tokenService.findByToken(token).getUsername();
        adminService.changePassword(password, username);
        model.addAttribute("info", "Password was reset");
        return loginPage(principal, model, username);
    }

}
