package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Dtos.ProductDto;
import com.ecommerce.admin.LIBRARY.ProductsService.ProductService;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.CategoryRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/add-product")
public class ProductUploadController {

    CategoryRepo categoryRepo;
    ProductService productService;

    @Autowired
    public ProductUploadController(CategoryRepo categoryRepo, ProductService productService) {
        this.categoryRepo = categoryRepo;
        this.productService = productService;
    }

    @PreAuthorize("hasAuthority(ADMIN)")
    @GetMapping
    public String addProductPage(Model model){
        model.addAttribute("categories", categoryRepo.findAllActiveCategories());
        return "PostAuth/add-product";
    }

    @PreAuthorize("hasAuthority(ADMIN)")
    @PostMapping
    public String saveProduct(@Valid ProductDto productDto,
                              BindingResult result,
                              Model model, HttpSession session){
        
        if(result.hasErrors()){
            model.addAttribute("result", result);
            return addProductPage(model);
        }


        productService.uploadProduct(productDto);

        session.setAttribute("info", productDto.getBrand()
                +" - "+productDto.getName()+" - "+productDto.getSize()+" added for price "+productDto.getSalePrice());

        return "redirect:/add-product?success";
    }



}
