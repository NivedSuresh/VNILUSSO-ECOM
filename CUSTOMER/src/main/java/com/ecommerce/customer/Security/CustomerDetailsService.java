package com.ecommerce.customer.Security;

import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerDetailsService implements UserDetailsService {
    CustomerService customerService;

    @Autowired
    public CustomerDetailsService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerService.findByEmail(username);
        if(customer==null)throw new UsernameNotFoundException("Invalid user");
        return CustomerDetails.builder().
                email(customer.getEmail())
                .password(customer.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(customer.getRole())))
                .build();
    }

}
