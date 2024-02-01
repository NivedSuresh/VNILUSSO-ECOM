package com.ecommerce.customer.LIBRARY.ProductsService.Impl;

import com.ecommerce.customer.LIBRARY.Exceptions.InvalidProductException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import com.ecommerce.customer.LIBRARY.Model.User.Product;
import com.ecommerce.customer.LIBRARY.Model.User.Wishlist;
import com.ecommerce.customer.LIBRARY.ProductsService.ProductService;
import com.ecommerce.customer.LIBRARY.ProductsService.WishlistService;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.WishlistRepo;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class WishlistServiceImpl implements WishlistService {

    CustomerService customerService;
    ProductService productService;

    @Autowired
    public WishlistServiceImpl(CustomerService customerService, WishlistRepo wishlistRepo,
                               ProductService productService) {
        this.customerService = customerService;
        this.wishlistRepo = wishlistRepo;
        this.productService = productService;
    }

    WishlistRepo wishlistRepo;

    public WishlistServiceImpl(WishlistRepo wishlistRepo) {
        this.wishlistRepo = wishlistRepo;
    }

    @Override
    public Wishlist findByCustomerEmail(String email) {
        try{return wishlistRepo.findByCustomerEmail(email);}
        catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to fetch wallet, try again later!");
        }
    }

    @Transactional
    @Override
    public void addToWishlist(Long productId, String email) {
        try{
            Wishlist wishlist = findByCustomerEmail(email);
            Customer customer = customerService.findByEmail(email);
            if(customer==null)
                throw new InvalidStateException("","Sorry for the inconvenience, l-45");
            Optional<Product> optionalProduct = productService.findById(productId);
            Product product = null;
            if(optionalProduct.isPresent())
                product = optionalProduct.get();
            else
                throw new InvalidProductException("","Unable to fetch the product which you're trying to access, try after sometime.");

            if(wishlist==null){
                wishlist = new Wishlist(null, 1, customer);
            }

            if(exists(wishlist.getProducts(), product))
                return;

            wishlist.getProducts().add(product);
            wishlistRepo.save(wishlist);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to add to Wishlist, try after sometime!");
        }
    }

    @Override
    public void removeFromWishlist(Long productId, String email) {
        try{
            if(customerService.existsByEmail(email) && productService.existsById(productId)){
                Wishlist wishlist = customerService.getWishlist(email);
                if(wishlist==null)
                    throw new InvalidStateException("", "Sorry for the inconvenience");
                wishlistRepo.removeProductByProductId(wishlist.getId(), productId);
            }

        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException)
                throw e;
        }
    }

    private boolean exists(Set<Product> products, Product product) {
        for(Product p : products)
            if(Objects.equals(p.getId(), product.getId()))
                return true;

        return false;
    }
}
