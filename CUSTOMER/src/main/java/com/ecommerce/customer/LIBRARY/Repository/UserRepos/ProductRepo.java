package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Category;
import com.ecommerce.customer.LIBRARY.Model.User.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    Product findByName(String name);

    Optional<Product> findById(Long id);

    @Query("select p from Product as p where p.isDeleted = false and p.quantity>0 and p.onSale = true")
    List<Product> getActiveProductsOnSale();

    @Query("select p from Product  as p where p.isDeleted = false and p.quantity > 0 and p.category.isDeleted = false ")
    List<Product> getActiveProducts();


    @Query("SELECT p FROM Product p JOIN Category c ON p.name LIKE :partialName% AND c.category = :categoryName AND p.isDeleted=false")
    List<Product> findProductsByPartialNameAndCategory(@Param("partialName") String partialName, @Param("categoryName") String categoryName);

    //here both column name and search text is converted to lower case.
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT(:partialName, '%')) AND p.isDeleted = false")
    List<Product> findAllProductsStartingBy(@Param("partialName") String partialName);

    @Query("select p from Product p where p.category = :category and p.isDeleted = false ")
    List<Product> findActiveByCategory(Category category);

    boolean existsByName(String name);


    @Transactional @Modifying
    @Query(value = "DELETE FROM product_images_url WHERE product_id = :id AND images_urls = :imageUrl", nativeQuery = true)
    void deleteImageWithImageUrl(@Param("imageUrl") String imageUrl, @Param("id") Long id);

    @Query(value = "select count(*) from product_images_url where product_id = :id", nativeQuery = true)
    int getImageCount(@Param("id") Long id);

    @Modifying @Transactional
    @Query(nativeQuery = true, value = "insert into product_images_url values (:id, :fileUrl)")
    void uploadImages(Long id, String fileUrl);

    @Modifying @Transactional
    @Query("update Product as p set p.isDeleted = true where p.id = :id")
    void disableProduct(Long id);

    @Modifying @Transactional
    @Query("update Product p set p.isDeleted = false where p.id = :id")
    void enableProduct(Long id);

    @Query("select p from Product  as p where p.isDeleted = false and p.quantity > 0 order by p.salePrice asc")
    List<Product> sortProductsFromLowToHigh();

    @Query("select p from Product  as p where p.isDeleted = false and p.quantity > 0 order by p.salePrice desc")
    List<Product> sortProductsFromHighToLow();

    @Query(nativeQuery = true, value = "select max(sale_price) from products")
    Double findMaxPrice();

    @Query("SELECT p FROM Product p WHERE p.id in :ids and  p.salePrice <= :maxPrice AND p.salePrice >= :minPrice AND p.category IN :categories")
    List<Product> findProductsByFilter(@Param("maxPrice") Double maxPrice, @Param("minPrice") Double minPrice,
                                       @Param("ids") List<Long> ids,
                                       @Param("categories") List<Category> categories);

    @Query("select distinct p.size from Product as p")
    List<String> findDistinctSizes();

    @Query(nativeQuery = true, value = "select product_product_id from product_size where size in :sizes")
    List<Long> findIdOfProductsWithSizes(List<String> sizes);

    @Query("select p.quantity from Product as p where p.id = :id")
    Integer getProductQuantity(Long id);

    @Query("update Product p set p.quantity = :quantity where p.id = :id")
    @Modifying @Transactional
    void setProductQuantity(int quantity, Long id);

    @Query("select count(p) from Product p where p.quantity > 0 and p.category = :category")
    Integer isQuantityZeroForCategory(Category category);

    @Query("select count(p) from Product  as p where p.isDeleted = false and p.quantity > 0 and p.category.isDeleted = false ")
    Integer getActiveProductsCount();

    @Modifying @Transactional
    @Query("update Product as p set p.salePrice = ROUND (p.salePrice* :decimalToBeMultipliedWith, 2), p.onSale = :onSale where p.category.id = :id")
    void discountForProductsFromCategory(Long id, Double decimalToBeMultipliedWith, boolean onSale);


    @Modifying @Transactional
    @Query("update Product as p set p.salePrice = ROUND((p.salePrice * 100/(100 - :offPercentage)), 2), p.onSale = :onSale where p.category.id = :id")
    void resetDiscountForProductsFromCategory(Long id, Double offPercentage, boolean onSale);

}
