package com.ecommerce.admin.LIBRARY.Service;

import com.ecommerce.admin.LIBRARY.Dtos.CustomerDto;
import com.ecommerce.admin.LIBRARY.Model.User.Cart;
import com.ecommerce.admin.LIBRARY.Model.User.Customer;
import com.ecommerce.admin.LIBRARY.Model.User.Wallet;
import com.ecommerce.admin.LIBRARY.Model.User.Wishlist;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;

public interface CustomerService {
    Customer findByEmail(String email);

    boolean existsByEmail(String email);

    boolean isBlocked(String email);

    void save(CustomerDto customerDto, boolean hashPassword);

    void blockCustomer(Long id);

    boolean existsById(Long id);

    boolean isDeleted(String email);

    void unBlockCustomer(Long id);

    Cart findCart(String email);


    CustomerDto getCustomerDto(String email);

    boolean updateProfile(String email, String username, String phoneNumber, Principal principal, HttpSession session);

    void updateEmail(String email, Principal principal);

    void finishUpdateEmail(String newEmail, String currentEmail);

    void generateTokenForResetPassword(String email, boolean forgot);

    void resetPasswordValidateToken(String email, String token, String password);

    Wishlist getWishlist(String email);

    Wallet getWallet(String email);

    Integer couponUsedCountByCustomer(Long customerId, Long couponId);

    void referUser(String principalEmail, String emailToBeReferred, boolean isEnabled);

    String getCustomerAuthority(String name);

    boolean updateIfOIDCUser(CustomerDto customerDto);

}
