package com.ecommerce.customer.LIBRARY.ProductsService;


import com.ecommerce.customer.LIBRARY.Dtos.FilterDto;
import com.ecommerce.customer.LIBRARY.Dtos.ProductDto;
import com.ecommerce.customer.LIBRARY.Model.User.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    void uploadProduct(ProductDto productDto);

    Product findByName(String name);

    List<Product> getActiveProductsOnSale();


    List<Product> findAll();

    List<Product> findByNameAndCategory(String name, String category);

    List<Product> findAllProductsStartingBy(String name);

    Optional<Product> findById(Long id);

    List<Product> findByCategory(String category);

    void saveTextBasedData(Long id, Product product);

    boolean deleteImageWithIdAndImageUrl(Long id, List<String> imageUrl);

    List<Product> findActiveProducts();

    void uploadImages(Long id, List<MultipartFile> images);

    void disableProduct(Long id);

    void enableProduct(Long id);

    List<Product> sortBy(Integer option);

    List<Product> filter(FilterDto filter);

    List<String> findDistinctSizes();

    Integer getProductQuantity(Long id);

    void setProductQuantity(int quantity, Long id);

    void save(Product product);

    boolean existsById(Long id);

    public void setQuantity(Long id, Integer quantity);
}
