package com.ecommerce.customer.LIBRARY.ProductsService;

import com.ecommerce.customer.LIBRARY.Dtos.CategoryDto;
import com.ecommerce.customer.LIBRARY.Model.User.Category;

import java.util.List;

public interface CategoryService {

    void saveCategory(CategoryDto categoryDto);

    List<Category> findAll();

    List<Category> findAllActiveCategories();

    boolean existsById(Long id);

    void softDelete(Long id);

    void enableCategory(Long id);

    void applyDiscountForCategory(Long id, Double offPercentage);

    void resetDiscountForCategory(Long id);
}
