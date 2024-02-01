package com.ecommerce.customer.Handler;

import com.ecommerce.customer.LIBRARY.ProductsService.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    CartService cartService;

    public CustomSuccessHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        cartService.dealWithSessionCartIfExists(request.getSession(), authentication.getName());
        new DefaultRedirectStrategy().sendRedirect(request,response,"/");
    }
}
