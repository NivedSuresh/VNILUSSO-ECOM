package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Dtos.AdminDto;
import com.ecommerce.admin.LIBRARY.Service.AdminService;
import com.ecommerce.admin.LIBRARY.Service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
public class CreateAdminController {

    private final AdminService adminService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    @Autowired
    public CreateAdminController(AdminService adminService, BCryptPasswordEncoder passwordEncoder,
                                 CustomerService customerService) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
    }



    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping("/create")
    public String createView(Model model){
        model.addAttribute("adminDto", new AdminDto());
        return "PostAuth/createView";
    }

    @PostMapping("/create-save")
    public String createSave (@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                                BindingResult result, Model model){
        try{
            if(result.hasErrors()){
                model.addAttribute("adminDto", adminDto);
                model.addAttribute("error","Input fields doesn't match the constraints");
                return "PostAuth/createView";
            }
            else if(adminService.adminExists(adminDto.getUsername())||
                    customerService.existsByEmail(adminDto.getUsername())){
                model.addAttribute("error", "This email is already registered");
                return "PostAuth/createView";
            }
            if(Objects.equals(adminDto.getPassword(), adminDto.getRepeatPassword())){
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                return "redirect:/create?success";
            }else{
                model.addAttribute("error", "password input should match");
            }
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "Try again after sometime");
        }
        return "/PostAuth/createView";
    }


}
