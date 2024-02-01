package com.ecommerce.customer.Handler;

import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {


    CartService cartService;
    CustomerService customerService;

    public OAuthSuccessHandler(CartService cartService, CustomerService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        cartService.dealWithSessionCartIfExists(request.getSession(), authentication.getName());
        new DefaultRedirectStrategy().sendRedirect(request,response,"/");
    }

}
