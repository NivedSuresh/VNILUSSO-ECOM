package com.ecommerce.customer.Auth;

import com.ecommerce.customer.LIBRARY.Exceptions.CustomerBlockedException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidLoginMethodException;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@Controller
public class CustomerAuthenticationProvider extends DaoAuthenticationProvider {

    CustomerService customerService;

    @Autowired
    public CustomerAuthenticationProvider(CustomerService customerService,
                                          UserDetailsService userDetailsService,
                                          PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String role = customerService.getCustomerAuthority(authentication.getName());
        if(role==null || role.equals("OIDC_USER")){
            throw new InvalidLoginMethodException("The email which you're trying to login with was registered using Google");
        }

        authentication = super.authenticate(authentication);

        try{
            String email = authentication.getName();

            if(customerService.existsByEmail(email)){
                if(customerService.isBlocked(email)){
                    throw new CustomerBlockedException("User blocked", "This account has been blocked");
                }
                else if (customerService.isDeleted(email)) {
                    throw new UsernameNotFoundException("Invalid user");
                }
                return authentication;
            }else{
                throw new UsernameNotFoundException("Invalid user");
            }
        }
        catch (Exception e){
            throw new AuthenticationServiceException("Failed to authorize user");
        }
    }


}
