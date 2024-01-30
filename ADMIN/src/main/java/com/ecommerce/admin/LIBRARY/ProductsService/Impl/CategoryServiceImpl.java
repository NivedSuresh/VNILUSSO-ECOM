package com.ecommerce.admin.LIBRARY.ProductsService.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.CategoryDto;
import com.ecommerce.admin.LIBRARY.Exceptions.CannotDeleteCategoryException;
import com.ecommerce.admin.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.admin.LIBRARY.Model.User.Category;
import com.ecommerce.admin.LIBRARY.ProductsService.CategoryService;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.CategoryRepo;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.ProductRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {


    CategoryRepo categoryRepo;
    ProductRepo productRepo;


    public CategoryServiceImpl(CategoryRepo categoryRepo, ProductRepo productRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
    }

    @Override
    public void saveCategory(CategoryDto categoryDto) {
        try{
            if(categoryRepo.existsByCategory(categoryDto.getCategory())) return;
            Category category = new Category();
            category.setCategory(categoryDto.getCategory());
            category.setDeleted(categoryDto.isDeleted());
            category.setAvg_price(0);
            category.setProducts(0);
            category.setOrders(0L);
            categoryRepo.save(category);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public List<Category> findAll() {
        try{return categoryRepo.findAll();}
        catch (Exception e){e.printStackTrace();return Collections.emptyList();
    }}

    @Override
    public List<Category> findAllActiveCategories() {
        try {return categoryRepo.findAllActiveCategories();}
        catch (Exception e) {e.printStackTrace();return Collections.emptyList();}}

    @Override
    public boolean existsById(Long id) {
        try{return categoryRepo.existsById(id);}
        catch (Exception e){e.printStackTrace();
            throw new InvalidStateException("", "Unable to connect to server, try again later!");
    }}

    @Override
    public void softDelete(Long id) {
        try{
            Optional<Category> optional = categoryRepo.findById(id);
            if(optional.isPresent()){
                if(productRepo.isQuantityZeroForCategory(optional.get())==0)
                    categoryRepo.softDelete(id);
                else throw new CannotDeleteCategoryException("Category Cannot be disabled as there are products still " +
                        "in stock for this category.");}
        }catch (Exception e){e.printStackTrace();
            if(e instanceof  CannotDeleteCategoryException) throw e;
    }}

    @Override
    public void enableCategory(Long id) {
        try{categoryRepo.enableCategory(id);}
        catch (Exception e){e.printStackTrace();}
    }

    @Transactional
    @Override
    public void applyDiscountForCategory(Long id, Double offPercentage) {
        try{
            if(offPercentage>80 || offPercentage<0)
                throw new InvalidStateException("","Cannot Apply Discount for Categories!");
            categoryRepo.applyDiscountForCategory(id, offPercentage);
            productRepo.discountForProductsFromCategory(id, 1.0-(offPercentage/100), true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void resetDiscountForCategory(Long id) {
        try{
            Double discountPercentage = categoryRepo.findDiscountAppliedForCategoryById(id);
            if(discountPercentage==null || discountPercentage<=0.0) return;
            categoryRepo.resetDiscountForCategory(id);
            productRepo.resetDiscountForProductsFromCategory(id, discountPercentage, false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
