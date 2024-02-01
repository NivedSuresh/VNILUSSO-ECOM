package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Dtos.CartDto;
import com.ecommerce.customer.LIBRARY.Dtos.FilterDto;
import com.ecommerce.customer.LIBRARY.Model.User.Category;
import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.ProductsService.ProductService;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CategoryRepo;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    ProductService productService;
    CategoryRepo categoryRepo;
    CsrfTokenRepository csrfTokenRepository;
    CustomerService customerService;
    CartService cartService;

    @Autowired
    public HomeController(ProductService productService,
                          CategoryRepo categoryRepo,
                          CsrfTokenRepository csrfTokenRepository,
                          CustomerService customerService,
                          CartService cartService) {
        this.productService = productService;
        this.categoryRepo = categoryRepo;
        this.csrfTokenRepository = csrfTokenRepository;
        this.customerService = customerService;
        this.cartService = cartService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder, HttpSession session){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @ModelAttribute("categories")
    public List<Category> categories(){
        return categoryRepo.findAllActiveCategories();
    }

    @GetMapping("/static")
    public String staticPage(){
        return "redirect:/";
    }

    @GetMapping("/")
    public String homePage(Model model, HttpServletRequest request, Principal principal){

        CsrfToken token = csrfTokenRepository.generateToken(request);
        HttpSession session = request.getSession(true);
        session.setAttribute("_csrf", token);

        if(principal!=null)
            session.setAttribute("cartSize", cartService.getCartSize(principal.getName()));
        else{
            CartDto cart = (CartDto) session.getAttribute("cart");
            session.setAttribute("cartSize", cart!=null?cart.getCartItems().size():0);
        }
        model.addAttribute("products", productService.getActiveProductsOnSale());
        return "index";
    }

    @GetMapping("/shop")
    public String shopView(Model model, HttpServletRequest request){

        CsrfToken token = csrfTokenRepository.generateToken(request);

        model.addAttribute("_csrf", token);
        model.addAttribute("sizes", productService.findDistinctSizes());
        model.addAttribute("products", productService.findActiveProducts());

        return "shop";
    }


    @GetMapping("/search")
    public String getSearch(){
        return "redirect:shop";
    }


    @PostMapping("/search")
    public String search(@RequestParam(required = false) String key,
                         @RequestParam(required = false, value = "category") String category
                         ,Model model){

        model.addAttribute("sizes", productService.findDistinctSizes());
        if(category!=null){
            model.addAttribute("products", productService.findByCategory(category));
            return "shop";
        }

        if(key==null){
            model.addAttribute("products", productService.findActiveProducts());
            return "shop";
        }

        model.addAttribute("key", key);
        model.addAttribute("products", productService.findAllProductsStartingBy(key));

        return "shop";
    }

    @PostMapping("/sort")
    public String sortBy(@RequestParam(value = "option", required = false) Integer option,
                         Model model){
        model.addAttribute("sizes", productService.findDistinctSizes());
        model.addAttribute("option", option);
        model.addAttribute("products", productService.sortBy(option));
        return "/shop";
    }

    @PostMapping("/filter")
    public String filterBy(FilterDto filter, Model model){
        model.addAttribute("sizes", productService.findDistinctSizes());
        model.addAttribute("categories", categoryRepo.findAllActiveCategories());
        model.addAttribute("products", productService.filter(filter));
        return "/shop";
    }



}
