package com.ecommerce.admin.LIBRARY.ProductsService.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.CouponDto;
import com.ecommerce.admin.LIBRARY.Exceptions.CannotApplyCouponException;
import com.ecommerce.admin.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.admin.LIBRARY.Model.User.Cart;
import com.ecommerce.admin.LIBRARY.Model.User.Coupon;
import com.ecommerce.admin.LIBRARY.Model.User.Customer;
import com.ecommerce.admin.LIBRARY.ProductsService.CartService;
import com.ecommerce.admin.LIBRARY.ProductsService.CouponService;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.CartItemRepo;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.CartRepo;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.CouponRepo;
import com.ecommerce.admin.LIBRARY.Service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class CouponServiceImpl implements CouponService {

    CouponRepo couponRepo;
    CustomerService customerService;
    CartRepo cartRepo;
    CartService cartService;

    CartItemRepo cartItemRepo;


    public CouponServiceImpl(CouponRepo couponRepo,
                             CustomerService customerService, CartRepo cartRepo,
                             CartService cartService, CartItemRepo cartItemRepo) {
        this.couponRepo = couponRepo;
        this.customerService = customerService;
        this.cartRepo = cartRepo;
        this.cartService = cartService;
        this.cartItemRepo = cartItemRepo;
    }

    @Override
    public List<Coupon> findAll() {
        try{
            return couponRepo.findAll();
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("","Unable to save coupon, try again after sometime!");}
    }

    @Override
    public void save(Coupon coupon) {
        try{
           couponRepo.save(coupon);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("","Unable to save coupon, try again after sometime!");
        }
    }

    @Override
    public Coupon dtoToEntity(CouponDto coupon, Long id) {
        return new Coupon(id, coupon.getCoupon(), coupon.getOffPercentage(),
                coupon.getMaxOffAmount(), coupon.getUsageAllowedPerCustomer(), new HashSet<>(),
                coupon.getCouponExpiryDate(), Objects.equals(coupon.getIsActive(), "true"), coupon.getMinSpend());
    }

    @Override
    public boolean existsById(Long id) {
        try{
            return couponRepo.existsById(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, coupon-s l-74");
        }
    }

    @Override
    public Coupon findById(Long id) {
        Optional<Coupon> coupon;
        try{coupon = couponRepo.findById(id);}
        catch (Exception e){
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, coupon-s l-79");
        }
        if(coupon.isEmpty()) throw new InvalidStateException("","Couldn't find the coupon you're looking for");
        return coupon.get();
    }

    @Override
    public CouponDto entityToDto(Coupon coupon) {
        return new CouponDto(coupon.getId(), coupon.getCoupon(),
                coupon.getDiscountPercentage(), coupon.getMaxDiscountAmount(),
                coupon.getUsageAllowedPerCustomer(), coupon.getIsActive().toString(), coupon.getExpiryDate(), coupon.getMinSpend());
    }

    @Transactional
    @Override
    public void applyCoupon(String email, String couponCode) {
        try{
            Coupon coupon = validateAndGetCoupon(couponCode);

            Customer customer = customerService.findByEmail(email);
            if(customerService.couponUsedCountByCustomer(customer.getId(), coupon.getId())
                    >= coupon.getUsageAllowedPerCustomer())
                throw new CannotApplyCouponException("Coupon limit exceeded");

            Cart cart = validateCartBeforeCouponApply(customer, coupon);

            if(cart.getCoupon()!=null && Objects.equals(cart.getCoupon().getId(), coupon.getId()))
                return;

            //if discount of total price exceeds max discount limit of coupon
            // create a temporary discount percentage and apply on cart!
            Double discountPercentage =
                    Math.min(cartService.calculateDiscountPercentage(cart.getTotalPrice(), coupon.getMaxDiscountAmount()),
                            coupon.getDiscountPercentage());

            cartService.setCouponForCart(cart.getId(), coupon);
            cartService.updateCartItemsPriceAfterNewCoupon(cart.getId(), discountPercentage);
            cartService.setCartTotalPrice(cart.getId(),
                    cartService.getCartTotal(cart.getId()));
        }catch (Exception e){
            if(e instanceof CannotApplyCouponException) throw e;
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, coupon-s l-95");
        }

    }


    //cart total price will be reduced here, if total discount is exceeding coupon allowed limit
    //then coupon allowed limit will be deducted. Before coupon is applied all constraints
    //should be checked, ie check if a coupon is already applied, deal with it accordingly.
    private Cart validateCartBeforeCouponApply(Customer customer, Coupon coupon) {
        Cart cart = customer.getCart();

        if(cart==null || cart.getCartItems().isEmpty())
            throw new CannotApplyCouponException("Cannot apply coupon on empty cart!");

        if(cart.getCoupon()!=null && !Objects.equals(cart.getCoupon().getId(), coupon.getId()))
            throw new CannotApplyCouponException("A coupon is already applied for this cart");

        if(cart.getTotalPrice()<coupon.getMinSpend())
            throw new CannotApplyCouponException("Cannot apply coupon as Cart doesn't meet the minimum spend requirement for the applicable coupon!");

        return cart;
    }

    private Coupon validateAndGetCoupon(String couponCode) {
        try{
            Coupon coupon = couponRepo.findCouponByCoupon(couponCode);
            if(coupon==null || !coupon.getIsActive() || coupon.getExpiryDate().isBefore(LocalDateTime.now()))
                throw new CannotApplyCouponException("Coupon is either Invalid or expired!");
            return coupon;
        }catch (Exception e){
            if(e instanceof CannotApplyCouponException) throw e;
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, coupon-s l-141");
        }
    }

    @Transactional
    @Override
    public void removeCoupon(String email) {
        try{
            Cart cart = cartService.findByUsername(email);
            if(cart==null || cart.getCartItems()==null || cart.getCartItems().isEmpty())
                return;
            cartService.setCouponForCart(cart.getId(),  null);
            resetCartAndCartItems(cart);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, coupon-s l-150");
        }
    }

    @Override
    public List<Coupon> findAvailableForCustomer(Cart cart) {
        return couponRepo.findAvailableCouponsForCustomer(LocalDateTime.now(), cart.getTotalPrice(), cart.getCustomer().getId());
    }

    @Transactional
    public void resetCartAndCartItems(Cart cart) {
        try{
            cartItemRepo.resetCartItemsPrice(cart.getId());
            cartRepo.setTotalPrice(cart.getId(), cartService.getCartTotalExcludeCouponDiscount(cart.getId()));
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, coupon-s l-161");
        }

    }
}
