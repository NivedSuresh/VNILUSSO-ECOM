package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findByCategory(String category);

    boolean existsByCategory(String category);

    @Modifying
    @Query(value = "UPDATE categories AS c " +
            "SET products = (SELECT COUNT(*) FROM products AS p WHERE p.category_id = c.category_id)+:num " +
            "WHERE category = :category",
            nativeQuery = true)
    void setTotalProductsPerCategory(@Param("category") String category,@Param("num") int num);


    @Query(nativeQuery = true, value = "select * from categories as c where c.is_deleted = false")
    List<Category> findAllActiveCategories();


    @Query("update Category c set c.isDeleted = true where c.id = :id")
    @Modifying
    @Transactional
    void softDelete(Long id);


    @Modifying @Transactional
    @Query("update Category c set c.isDeleted = false  where c.id =:id")
    void enableCategory(Long id);


    @Transactional @Modifying
    void removeCategoryById(Long id);

    @Query("select count(c) from Category c where c.isDeleted = false")
    Integer getActiveCategoriesCount();

    @Modifying @Transactional
    @Query("update Category as c set c.discountPercentage = :offPercentage where c.id = :id")
    void applyDiscountForCategory(Long id, Double offPercentage);

    @Modifying @Transactional
    @Query("UPDATE Category c set c.discountPercentage = 0.0 where c.id = :id")
    void resetDiscountForCategory(Long id);

    @Query("select c.discountPercentage from Category as c where c.id = :id")
    Double findDiscountAppliedForCategoryById(Long id);
}
