package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Model.User.Product;
import com.ecommerce.admin.LIBRARY.ProductsService.CategoryService;
import com.ecommerce.admin.LIBRARY.ProductsService.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequestMapping("/products")
@Controller
public class ProductController {

    ProductService productService;
    CategoryService categoryService;


    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping
    public String getAllProducts(Model model){
        model.addAttribute("products", productService.findAll());
        return "/PostAuth/allProducts";
    }


    @PostMapping("/search")
    public String searchInAdminAllProducts(@RequestParam(value = "product-name", required = false) String name,
                                           @RequestParam(value = "category") String category, Model model){

        if(name == null){
            if(Objects.equals(category, "all-category"))
                return "redirect:/products";
                else{
                model.addAttribute("products", productService.findByCategory(category));
                return "/PostAuth/allProducts";
            }
        }

        //name won't be null but leave it there for better readability
        if(Objects.equals(category, "all-category") && name!=null){
            model.addAttribute("products", productService.findAllProductsStartingBy(name));
            return "/PostAuth/allProducts";
        }

        model.addAttribute("products", productService.findByNameAndCategory(name, category));
        return "/PostAuth/allProducts";

    }

    @GetMapping ("/edit/{id}")
    public String editProductView(@PathVariable("id") Long id, Model model){
        model.addAttribute("product", productService.findById(id).get());
        model.addAttribute("categories", categoryService.findAllActiveCategories());
        return "/PostAuth/edit-product";
    }


    @PostMapping("/edit/{id}")
    public String afterEditProductView(@PathVariable("id") Long id, Model model){
        model.addAttribute("product", productService.findById(id).get());
        model.addAttribute("categories", categoryService.findAllActiveCategories());
        return "/PostAuth/edit-product";
    }


    @PostMapping("/edit/save/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid  Product product,
                                BindingResult result, @RequestParam(value = "onSale", required = false) boolean onSale){
        if(result.hasErrors()){
            return "redirect:/products/edit/"+id+"?error";
        }
        product.setOnSale(onSale);
        productService.saveTextBasedData(id, product);
        return "redirect:/products/edit/"+id+"?success";
    }

    @Transactional
    @PostMapping("/delete/{id}/{imageUrl}")
    public String deleteProductImage(@PathVariable("id") Long id,
                                     @PathVariable("imageUrl") String imageUrl){

        if(productService.deleteImageWithIdAndImageUrl(id, Arrays.asList(imageUrl))){
            return "redirect:/products/edit/"+id+"?deleted";
        }else{
            return "redirect:/products/edit/"+id+"?error";
        }

    }

    @PostMapping("/edit/upload-image/{id}")
    public String uploadNewImage(@PathVariable("id") Long id, @RequestParam("images") List<MultipartFile> images){
        productService.uploadImages(id, images);
        return "redirect:/products/edit/"+id+"?uploaded";
    }

    @PostMapping("/disable/{id}")
    public String disableProduct(@PathVariable("id") Long id){
        productService.disableProduct(id);
        return "redirect:/products";
    }

    @PostMapping("/enable/{id}")
    public String enableProduct(@PathVariable("id") Long id){
        productService.enableProduct(id);
        return "redirect:/products";
    }

}
