package com.ecommerce.customer.LIBRARY.ProductsService.Impl;

import com.ecommerce.customer.LIBRARY.Dtos.FilterDto;
import com.ecommerce.customer.LIBRARY.Dtos.ProductDto;
import com.ecommerce.customer.LIBRARY.Exceptions.ImageProcessException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidProductException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Model.User.Category;
import com.ecommerce.customer.LIBRARY.Model.User.Product;
import com.ecommerce.customer.LIBRARY.ProductsService.ProductService;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CategoryRepo;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.ProductRepo;
import com.ecommerce.customer.LIBRARY.Utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    ProductRepo productRepo;
    CategoryRepo categoryRepo;
    FileUtil fileUtil;


    @Autowired
    public ProductServiceImpl(ProductRepo productRepo, FileUtil fileUtil, CategoryRepo categoryRepo) {
        this.productRepo = productRepo;
        this.fileUtil = fileUtil;
        this.categoryRepo = categoryRepo;
    }

    @Transactional
    @Override
    public void uploadProduct(ProductDto productDto) {
        try{
            if(productDto.getImagesUrls()==null){
                throw new ImageProcessException("Image process Exception", "Unable to process" +
                        " as Images weren't readable or minimum image count didn't meet.");
            }

            if(productRepo.existsByName(productDto.getName())){
                throw new ImageProcessException("Product exists", "There is already another product in the database with similar name.");
            }

            categoryRepo.setTotalProductsPerCategory(productDto.getCategory().getCategory(), 1);
            productRepo.save(
                    Product.builder().brand(productDto.getBrand())
                            .costPrice(productDto.getCostPrice())
                            .salePrice(productDto.getSalePrice())
                            .description(productDto.getDescription())
                            .category(productDto.getCategory())
                            .isDeleted(false).onSale(false)
                            .quantity(productDto.getQuantity())
                            .name(productDto.getName())
                            .size(productDto.getSize())
                            .imagesUrls(fileUtil.uploadToLocalAndReadyImages(productDto.getImagesUrls()))
                            .build());
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof ImageProcessException) throw e;
            throw new InvalidStateException("", "Unable to complete operation, try again later");
        }

    }

    @Override
    public void saveTextBasedData(Long id, Product p) {
        try{
            if(productRepo.existsById(id)){productRepo.save(p);}
            else{
                throw new InvalidProductException("Invalid", "Unable to find the product which you're tying to update");
            }
        }catch (Exception e){
            if (e instanceof  InvalidProductException) throw e;
            throw new InvalidStateException("", "Couldn't complete operation, try again later! order-s l-112");
        }
    }

    @Override
    public boolean deleteImageWithIdAndImageUrl(Long id, List<String> imagesUrl) {

        try{
            if(id==null||imagesUrl==null||imagesUrl.get(0)==null||
                    !productRepo.existsById(id)||productRepo.getImageCount(id)<=2){
                throw new ImageProcessException("","");
            }
            if(imagesUrl.size()==1)
                productRepo.deleteImageWithImageUrl(imagesUrl.get(0), id);
            else {
                //add function to delete all images if needed
            }
            fileUtil.deleteImagesFromFile(imagesUrl);
            return true;
        }catch (Exception e){
            throw new ImageProcessException("Unable to delete", "Image or product doesn't " +
                    "exist or the product image count has reached it's minimum.");
        }

    }

    @Override
    public List<Product> findActiveProducts() {
        try{
            return productRepo.getActiveProducts();
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to fetch active products, l-143");
        }

    }

    @Transactional
    @Override
    public void uploadImages(Long id, List<MultipartFile> images) {
        try{
            if(images==null || images.get(0)==null){
                throw new ImageProcessException("value is NUll", "Select an Image.");
            }else if(id==null || !productRepo.existsById(id)){
                throw new InvalidProductException("","Unable to find the product.");
            }

            List<String> fileUrls = fileUtil.uploadToLocalAndReadyImages(images);
            for(String fileUrl : fileUrls)
                productRepo.uploadImages(id, fileUrl);

        }catch (Exception e){
            if(e instanceof ImageProcessException||e instanceof InvalidProductException){
                throw e;
            }
            throw new ImageProcessException("","Unable to upload Image.");
        }

    }

    @Override
    public void disableProduct(Long id) {
        try{
            if(productRepo.existsById(id)){
                productRepo.disableProduct(id);
                Category category = productRepo.findById(id).get().getCategory();
                categoryRepo.setTotalProductsPerCategory(category.getCategory(), category.getProducts()-1);
            }
            else
                throw new InvalidProductException("","Unable to disable the product, it's either Invalid or is already removed");
        }catch (Exception e){
            if(e instanceof  InvalidProductException)
                throw e;
        }
    }

    @Override
    public void enableProduct(Long id) {
        try{
            productRepo.enableProduct(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to enable Product!");
        }
    }

    @Override
    public List<Product> sortBy(Integer option) {
        try{
            if(option==null || option==1){
                return findActiveProducts();
            } else if (option==2) {
                return productRepo.sortProductsFromLowToHigh();
            }
            return productRepo.sortProductsFromHighToLow();
        }catch(Exception e){
            return findActiveProducts();
        }
    }

    @Override
    public List<Product> filter(FilterDto filter) {
        try{
            if(filter.getMin()==null)
                filter.setMin(0.0);

            if(filter.getMax()==null){
                filter.setMax(productRepo.findMaxPrice());
            }

            if(filter.getCategory()==null || filter.getCategory().get(0)==null){
                filter.setCategory(categoryRepo.findAllActiveCategories().stream()
                        .map(Category::getCategory).collect(Collectors.toList()));
            }
            if(filter.getSizes()==null){
                filter.setSizes(productRepo.findDistinctSizes());
            }

            return productRepo.findProductsByFilter(filter.getMax(), filter.getMin(),
                    productRepo.findIdOfProductsWithSizes(filter.getSizes()),
                    filter.getCategory()
                            .stream()
                            .map(s -> categoryRepo.findByCategory(s))
                            .collect(Collectors.toList()));
        }catch(Exception e){
            return productRepo.getActiveProducts();
        }
    }

    ///cg handled
    @Override
    public List<String> findDistinctSizes() {
        try {
            return productRepo.findDistinctSizes();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to find distinct sizes.");
        }
    }

    @Override
    public Integer getProductQuantity(Long id) {
        try {
            return productRepo.getProductQuantity(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to get product quantity.");
        }
    }

    @Override
    public void setProductQuantity(int quantity, Long id) {
        try {
            productRepo.setProductQuantity(quantity, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to set product quantity.");
        }
    }

    @Override
    public void save(Product product) {
        try {
            productRepo.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to save the product.");
        }
    }

    public void setQuantity(Long id, Integer quantity) {
        try {
            productRepo.setProductQuantity(quantity, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to set product quantity.");
        }
    }

    @Override
    public boolean existsById(Long id) {
        try {
            return productRepo.existsById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to check if the product exists.");
        }
    }

    public Product findByName(String name) {
        try {
            return productRepo.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to find a product by name.");
        }
    }

    public List<Product> getActiveProductsOnSale() {
        try {
            return productRepo.getActiveProductsOnSale();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to get active products on sale.");
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            return productRepo.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to find all products.");
        }
    }

    @Override
    public List<Product> findByNameAndCategory(String name, String category) {
        try {
            return productRepo.findProductsByPartialNameAndCategory(name, category);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to find products by name and category.");
        }
    }

    @Override
    public List<Product> findAllProductsStartingBy(String name) {
        try {
            return productRepo.findAllProductsStartingBy(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("","Unable to find products starting with a name.");
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        try {
            return productRepo.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("","Unable to find a product by ID.");
        }
    }

    @Override
    public List<Product> findByCategory(String category) {
        try {
            return productRepo.findActiveByCategory(categoryRepo.findByCategory(category));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to find products by category.");
        }
    }



}
