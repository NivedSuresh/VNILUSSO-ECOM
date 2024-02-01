package com.ecommerce.customer.Controller;

import com.ecommerce.customer.LIBRARY.Exceptions.InvalidProductException;
import com.ecommerce.customer.LIBRARY.Model.User.Product;
import com.ecommerce.customer.LIBRARY.ProductsService.CategoryService;
import com.ecommerce.customer.LIBRARY.ProductsService.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


@Controller
public class ProductController {

    ProductService productService;
    CategoryService categoryService;

    CsrfTokenRepository csrfTokenRepository;

    @Autowired
    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             CsrfTokenRepository csrfTokenRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @GetMapping("/product-details/{id}")
    public String productDetails(@PathVariable("id") Long id, Model model, HttpServletRequest request){

        request.getSession(true);
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        request.getSession().setAttribute("_csrf", csrfToken.getToken());

        Product product = null;
        Optional<Product> optionalProduct = productService.findById(id);
        if(optionalProduct.isPresent()){
            product = optionalProduct.get();
            model.addAttribute("sizes", productService.findDistinctSizes());
            model.addAttribute("categories", categoryService.findAllActiveCategories());
            model.addAttribute("product", product);
            return "productView";
        }else{
            throw new InvalidProductException("Product invalid", "This product is not active anymore");
        }

    }

}
