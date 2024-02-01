package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Dtos.CustomerDto;
import com.ecommerce.customer.LIBRARY.Exceptions.SessionExpiredException;
import com.ecommerce.customer.LIBRARY.Service.AdminService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import com.ecommerce.customer.LIBRARY.Service.OtpService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/signup")
@Controller
public class SignUpController {

    CustomerService customerService;
    LoginController loginController;
    HomeController homeController;
    PasswordEncoder passwordEncoder;
    OtpService otpService;
    private final AdminService adminService;

    @Autowired
    public SignUpController(CustomerService customerService,
                            LoginController loginController,
                            HomeController homeController,
                            OtpService otpService,
                            PasswordEncoder passwordEncoder, AdminService adminService) {
        this.loginController = loginController;
        this.customerService = customerService;
        this.homeController = homeController;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping
    public String signupPage(Model model, String token, HttpSession session){
        model.addAttribute("customer",new CustomerDto(token));
        return "/login-register";
    }


    @GetMapping("/send-otp")
    public String sendOtp(){
        return "/signup-send-otp";
    }


    @PostMapping
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody(required = false) @Valid CustomerDto customerDto,
                                    BindingResult result, HttpSession session) {
        if (result.hasErrors()) {

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Validation error");
            response.put("errors", result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response);
        }
        if(adminService.adminExists(customerDto.getEmail()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email Already Exists!");
        if (customerService.existsByEmail(customerDto.getEmail())) {
            if(customerService.updateIfOIDCUser(customerDto)){
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Initiated OIDC to Customer");
            }
            // Account already exists, return an error response
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email Already Exists!");
        }

        if (!Objects.equals(customerDto.getPassword(), customerDto.getConfirmPassword())) {
            // Password and confirm password do not match, return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password doesn't match");
        }


        customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
        session.setAttribute("customerDto", customerDto);
        otpService.generateOtp(customerDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }



    @PostMapping("/verify-otp")
    private String verifyOtp(@RequestParam("otp") String otp, HttpSession session){

        CustomerDto customerDto = (CustomerDto) session.getAttribute("customerDto");
        if(customerDto==null){
            throw new SessionExpiredException("Session Expired", "This session has Expired or is Invalid");
        }

        //Will throw exception if otp is Invalid
        otpService.validateOtp(otp, customerDto.getEmail());
        otpService.setUsed(otpService.findByOtp(otp));
        customerService.save(customerDto, false);

        //if time persists try implementing task scheduling to remove this attribute
        session.removeAttribute("customerDto");
        return "redirect:/signup?success";
    }
}
