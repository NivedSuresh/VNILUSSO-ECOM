package com.ecommerce.admin.Auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
public class CustomAuthenticationDetails extends WebAuthenticationDetails {
    String otp;
    public CustomAuthenticationDetails(HttpServletRequest context) {
        super(context);
        this.otp = context.getParameter("otp");
    }
}
