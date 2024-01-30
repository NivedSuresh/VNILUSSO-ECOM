package com.ecommerce.admin.Handler;

import com.ecommerce.admin.LIBRARY.Service.OtpService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    OtpService otpService;

    @Autowired
    public CustomSuccessHandler(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for(GrantedAuthority authority : authorities){
            redirectUrl = authority.getAuthority().equals("ADMIN")?"/dashboard":"/login?error";
            break;
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
