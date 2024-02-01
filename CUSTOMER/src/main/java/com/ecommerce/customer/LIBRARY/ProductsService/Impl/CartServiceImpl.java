package com.ecommerce.customer.LIBRARY.ProductsService.Impl;

import com.ecommerce.customer.LIBRARY.Dtos.CartDto;
import com.ecommerce.customer.LIBRARY.Dtos.CartItemDto;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidProductException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Exceptions.UnableToFindCartException;
import com.ecommerce.customer.LIBRARY.Model.User.*;
import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.ProductsService.ProductService;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CartItemRepo;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CartRepo;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    CartRepo cartRepo;
    CartItemRepo cartItemRepo;
    CustomerService customerService;

    ProductService productService;

    @Autowired
    public CartServiceImpl(CartRepo cartRepo, CustomerService customerService,
                           CartItemRepo cartItemRepo, ProductService productService) {
        this.customerService = customerService;
        this.cartItemRepo = cartItemRepo;
        this.cartRepo = cartRepo;
        this.productService = productService;
    }


    @Transactional @Override
    public void addItem(Long id, String email, Integer quantity, String size) {

        try{
            Customer customer = customerService.findByEmail(email);
            if(customer==null){
                throw new InvalidStateException("", "Unable to find the account associated with this cart");
            }
            if(quantity==null) quantity=1;
            if(size==null) size = "free";

            Cart cart = customer.getCart();
            Optional<Product> optionalProduct = productService.findById(id);
            Product product = null;

            if(optionalProduct.isPresent())
                product = optionalProduct.get();
            else
                throw new InvalidProductException("",
                        "The product which you're trying to add doesn't exist");

            if(cart==null) cart = new Cart();

            //find if the item which you're trying to add already exists in the table.
            Set<CartItem> cartItems = cart.getCartItems();
            CartItem cartItem = null;
            if(cartItems!=null && !cartItems.isEmpty())
                cartItem = find(cartItems, id, size);

            if(cartItem == null){
                cartItem = new CartItem();
                cartItem.setQuantity(quantity);
                cartItem.setProduct(product);
                cartItem.setTotalPrice(product.getSalePrice()*quantity);
                cartItem.setSize(size);
                cart.setTotalItems(cart.getTotalItems()+1);
            }else{
                cartItem.setQuantity(cartItem.getQuantity()+quantity);
                cartItem.setTotalPrice(cartItem.getTotalPrice()+(product.getSalePrice()*quantity));
            }

            cart.setCustomer(customer);
            cart.setTotalPrice(cart.getTotalPrice()+ (cartItem.getProduct().getSalePrice()*quantity));
            cart.setCartItems(cartItems);

            if(cart.getId()!=null){
                cartItem.setCart(cart);
                cartItemRepo.save(cartItem);
                if(cart.getCoupon()!=null) updateCartBasedOnAppliedCoupon(cart);
            }
            else{
                Cart saved = cartRepo.save(cart);
                cartItem.setCart(saved);
                cartItemRepo.save(cartItem);
            }

        }catch (Exception e){
            if(e instanceof InvalidStateException || e instanceof InvalidProductException)
                throw e;
            e.printStackTrace();
        }

    }

    @Override
    public void addItemToCartSession(Long productId, HttpSession session, String size) {

        try{

            if(size==null) size="free";

            Optional<Product> optionalProduct = productService.findById(productId);
            Product product = null;

            if(optionalProduct.isPresent())
                product = optionalProduct.get();
            else
                throw new InvalidProductException("","Invalid entry");

            CartDto cart = (CartDto) session.getAttribute("cart");

            Map<String, CartItemDto> cartItems = null;


            if(cart==null){
                cart = new CartDto();
                cart.setId(0L);
                cart.setTotalItems(1);
                cart.setCustomer(null);
                cart.setTotalPrice(product.getSalePrice());
                cartItems = new HashMap<>();
            }else{
                cart.setTotalItems(cart.getTotalItems()+1);
                cart.setTotalPrice(cart.getTotalPrice()+product.getSalePrice());
                cartItems=cart.getCartItems();
            }

            String itemId = productId+"-"+size;

            if(!cartItems.containsKey(itemId)){
                cartItems.put(itemId, new CartItemDto(cart, product, 1,product.getSalePrice(),size));
            }else {
                CartItemDto cartItem = cartItems.get(itemId);
                cartItem.setQuantity(cartItem.getQuantity()+1);
                cartItem.setUnitPrice(cartItem.getUnitPrice()+product.getSalePrice());
            }

            cart.setCartItems(cartItems);
            session.setAttribute("cart", cart);
        }catch (Exception e){
            if(e instanceof InvalidProductException)
                throw e;
            e.printStackTrace();
        }
    }


    @Transactional @Override
    public void updateCart(String email, List<Long> cartItemsId, List<Integer> quantityPerItem) {
        try{
            Double totalPrice = 0.0;
            for(int i=0 ; i<cartItemsId.size() ; i++){

                if(quantityPerItem.get(i)<=0){
                    cartItemRepo.removeItem(cartItemsId.get(i));
                    continue;
                }

                Optional <CartItem> cartItemOptional = cartItemRepo.findById(cartItemsId.get(i));
                if(cartItemOptional.isPresent()){
                    CartItem cartItem = cartItemOptional.get();

                    cartItem.setQuantity(quantityPerItem.get(i));
                    cartItem.setTotalPrice(cartItem.getProduct().getSalePrice()*quantityPerItem.get(i));
                    cartItemRepo.save(cartItem);
                    totalPrice+=cartItem.getTotalPrice();
                }
            }
            Cart cart = findByUsername(email);
            if(cart.getCoupon()!=null) updateCartBasedOnAppliedCoupon(cart);
            else cartRepo.setTotalPrice(cart.getId(), totalPrice);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void updateCartForSession(List<String> cartItemsId, List<Integer> quantityPerItem, HttpSession session, String size) {

        try{
            CartDto cart = (CartDto) session.getAttribute("cart");

            if(cart==null)
                throw new UnableToFindCartException("unable to find the cart associated with the current session, l-200");

            Map<String, CartItemDto> cartItemsList = cart.getCartItems();
            double totalPrice= 0.0;

            for(int i=0 ; i<cartItemsId.size() ; i++){
                CartItemDto cartItemDto = cartItemsList.get(cartItemsId.get(i));

                if(quantityPerItem.get(i)<=0){
                    cartItemsList.remove(cartItemDto.getId());
                    continue;
                }else cartItemDto.setQuantity(quantityPerItem.get(i));

                cartItemDto.setUnitPrice(cartItemDto.getProduct().getSalePrice()*(cartItemDto.getQuantity()));
                totalPrice+=cartItemDto.getUnitPrice();
            }

            cart.setCartItems(cartItemsList);
            cart.setTotalPrice(totalPrice);
            session.setAttribute("cart", cart);
        }catch (Exception e){
            if(e instanceof UnableToFindCartException)
                throw e;
            e.printStackTrace();
        }
    }


    @Transactional
    @Override
    public void removeItem(Long id, String sessionItemId, HttpSession session, Principal principal, String size) {
        try{
            if(principal==null){
                if(sessionItemId==null) return;
                CartDto cart = (CartDto) session.getAttribute("cart");
                if(cart==null)
                    throw new UnableToFindCartException("Unable to find the cart associated with this session, try adding those items back in your cart again.");
                CartItemDto cartItemDto = cart.getCartItems().get(sessionItemId);
                cart.getCartItems().remove(sessionItemId);
                cart.setTotalPrice(cart.getTotalPrice()-cartItemDto.getUnitPrice());
                session.setAttribute("cart", cart);
                return;
            }

            Cart cart = findByUsername(principal.getName());
            if(cart==null)
                throw new UnableToFindCartException("Unable to process your request right now, try again later. l-257");

            if(cart.getCoupon()==null){
                cartRepo.setTotalPrice(cart.getId(),
                    cart.getTotalPrice()-cartItemRepo.getCartItemPriceById(id));
                cartItemRepo.removeItem(id);
            }else{
                cartItemRepo.removeItem(id);
                updateCartBasedOnAppliedCoupon(cart);
            }

        }catch (Exception e){
            if(e instanceof UnableToFindCartException)
                throw e;
            e.printStackTrace();
        }
    }



    private static CartItem find(Set<CartItem> cartItems, Long id, String size){
        for(CartItem cartItem : cartItems){
            if(cartItem.getProduct().getId().equals(id) && cartItem.getSize().equals(size))
                return cartItem;
        }
        return null;
    }

    @Override
    public void clearCart(Long id) {
        try{
            cartItemRepo.deleteByCartId(id);
            cartRepo.setTotalPrice(id, 0.0);
            cartRepo.emptyTotalItems(id);
            cartRepo.setCouponForCart(id, null);
        }catch (Exception e){
            throw new InvalidStateException("", "Unable to clear cart, l-235");
        }

    }

    @Override
    public Integer cartSize(Long id) {
        try{
            return cartRepo.getCartSize(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("Unable to fetch cart size", "Sorry for the Inconvenience, l-246");
        }

    }

    @Override
    public boolean existsById(Long cartId) {
        try{return cartRepo.existsById(cartId);}
        catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("Unable to check if cart exists", "Sorry for the Inconvenience, l-258");
        }
    }

    @Override
    public Cart findCartById(Long id) {
        try{return cartRepo.findCartById(id);}
        catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("Unable to fetch cart by Id", "Sorry for the Inconvenience, l-269");
        }
    }

    @Override
    public Double getCartTotal(Long cartId) {
        try{
            return cartRepo.getCartTotalIncludeCouponDiscount(cartId);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, cart-s l-316");
        }
    }

    @Override
    public void updateCartItemsPriceAfterNewCoupon(Long cartId , Double couponDiscountPercentage) {
        try{
            cartItemRepo.updateCartItemsPriceAfterNewCoupon(cartId,
                    1.0-(couponDiscountPercentage/100.0));
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, cart-s l-328");
        }

    }

    @Override
    public void setCouponForCart(Long cartId, Coupon coupon) {
        try{
            cartRepo.setCouponForCart(cartId, coupon);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, cart-s l-339");
        }
    }

    @Override
    public void setCartTotalPrice(Long cartId, Double totalPrice) {
        try{
            cartRepo.setTotalPrice(cartId, totalPrice);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, cart-s l-349");
        }
    }

    @Override
    public Double calculateDiscountPercentage(double totalPrice, Double maxDiscountAmount) {
        return (100*maxDiscountAmount)/totalPrice;
    }

    @Transactional
    @Override
    public void dealWithSessionCartIfExists(HttpSession session, String email) {
        CartDto cart = (CartDto) session.getAttribute("cart");
        if(cart!=null && !cart.getCartItems().isEmpty()){
            for(CartItemDto cartItemDto : cart.getCartItems().values()){
                addItem(cartItemDto.getProduct().getId(), email, cartItemDto.getQuantity(), cartItemDto.getSize());
            }
        }
        session.removeAttribute("cart");
    }


    @Override
    public Integer getCartSize(String name) {
        try{
            Cart cart = customerService.findCart(name);
            return cart!=null?cart.getCartItems().size():0;
        }catch (Exception e){
            throw new InvalidStateException("", "Unable to fetch cart size from database, l-43");
        }
    }

    @Override
    public Cart findByUsername(String username) {
        try{return cartRepo.findByCustomer(customerService.findByEmail(username));}
        catch(Exception e){throw new UnableToFindCartException("Unable to find cart, l-358");}
    }

    @Transactional
    public void updateCartBasedOnAppliedCoupon(Cart cart) {
        cart.setTotalPrice(getCartTotalExcludeCouponDiscount(cart.getId()));

        Double discountPercentage = Math.min(
                calculateDiscountPercentage(cart.getTotalPrice(), cart.getCoupon().getMaxDiscountAmount())
                ,
                cart.getCoupon().getDiscountPercentage());

        updateCartItemsPriceAfterNewCoupon(cart.getId() ,discountPercentage);
        setCartTotalPrice(cart.getId(), getCartTotal(cart.getId()));
    }

    public Double getCartTotalExcludeCouponDiscount(Long cartId) {
        try{
            return cartRepo.getCartTotalExcludingCoupon(cartId);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Couldn't complete the operation, try again later, cart-s l-385");
        }

    }

}
