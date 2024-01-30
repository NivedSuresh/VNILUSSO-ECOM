package com.ecommerce.admin.Controller;

import com.ecommerce.admin.LIBRARY.Dtos.CategoryDto;
import com.ecommerce.admin.LIBRARY.ProductsService.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/categories")
@Controller
public class CategoriesController {

    CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @InitBinder
    public void removeWhiteSpaces(WebDataBinder dataBinder){
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, ste);
    }

    @GetMapping
    public String viewCategories(Model model){
        model.addAttribute("categories", categoryService.findAll());
        return "/PostAuth/categories";
    }

    @GetMapping("/random")
    public String viewCategoriesss(Model model){
        model.addAttribute("categories", categoryService.findAll());
        return "/PostAuth/categories";
    }

    @GetMapping("/create")
    public String viewCategories2(){
        return "redirect:/categories";
    }

    @PostMapping("/create")
    public String create(@Valid CategoryDto categoryDto, BindingResult result,
                         @RequestParam(value = "isDeleted", required = false) boolean isDeleted,
                         Model model){

        if(result.hasErrors()){
            model.addAttribute("result", result);
            return viewCategories(model);
        }
        categoryDto.setDeleted(isDeleted);
        categoryService.saveCategory(categoryDto);
        return "redirect:/categories?success";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id){
        if(categoryService.existsById(id)){
            categoryService.softDelete(id);
        }
        return "redirect:/categories";
    }

    @PostMapping("/enable/{id}")
    public String enableCategory(@PathVariable Long id){
        if(categoryService.existsById(id)){
            categoryService.enableCategory(id);
        }
        return "redirect:/categories";
    }

    @PostMapping("/apply_off/{id}")
    public String applyOffForCategory(@PathVariable Long id,  Double offPercentage){
        categoryService.applyDiscountForCategory(id, offPercentage);
        return "redirect:/categories";
    }

    @PostMapping("reset_off/{id}")
    public String resetDiscount(@PathVariable("id") Long id){
        categoryService.resetDiscountForCategory(id);
        return "redirect:/categories";
    }


}
