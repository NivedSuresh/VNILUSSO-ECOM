package com.ecommerce.customer.Config;

import com.ecommerce.customer.Handler.CustomSuccessHandler;
import com.ecommerce.customer.Handler.OAuthSuccessHandler;
import com.ecommerce.customer.LIBRARY.Dtos.CustomerDto;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Service.AdminService;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import com.ecommerce.customer.Security.CustomerDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@EnableWebSecurity
@Configuration
public class CustomerSecurityConfig {

    CustomSuccessHandler customSuccessHandler;
    OAuthSuccessHandler oAuthSuccessHandler;
    CustomerService customerService;
    AdminService adminService;
    PasswordEncoder passwordEncoder;

    public CustomerSecurityConfig(CustomSuccessHandler customSuccessHandler, OAuthSuccessHandler oAuthSuccessHandler,
                                  CustomerService customerService, PasswordEncoder passwordEncoder, AdminService adminService) {
        this.customSuccessHandler = customSuccessHandler;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {

        http.csrf(csrf-> csrf
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/authenticate"))
                .ignoringRequestMatchers("/", "/product-details/**"));


        http.oauth2Login(oAuth -> oAuth.loginPage("/login")
                .userInfoEndpoint(ui -> ui.oidcUserService(oidcLoginHandler()))
                .successHandler(oAuthSuccessHandler)
        );

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/order/**","/address/**","/orders/**","/profile/**","/wishlist/**").authenticated()
                .requestMatchers("/wallet/**","/apply-coupon/**", "/checkout/**","/invoice/**").authenticated()
                .requestMatchers("/payment/**").authenticated()
                .anyRequest().permitAll());

        http.addFilterBefore(new CsrfFilter(new HttpSessionCsrfTokenRepository()), BasicAuthenticationFilter.class)
                .formLogin( form -> form
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/login")
                .loginProcessingUrl("/authenticate").successHandler(customSuccessHandler)
                );

        http.logout(logout -> logout
                .logoutSuccessUrl("/?logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .permitAll());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .invalidSessionUrl("/login?expired")
                .maximumSessions(1)
        );


        return http.build();
    }

    @Transactional
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcLoginHandler() {
        return userRequest -> {
            OidcUserService delegate = new OidcUserService();
            OAuth2User oidcUser = delegate.loadUser(userRequest);

            try{
                if(adminService.adminExists(oidcUser.getAttribute("email"))){
                    throw new InvalidStateException("","Email already registered with a different role!");
                }
                if(!customerService.existsByEmail(oidcUser.getAttribute("email"))){
                    customerService.save(oidcUserToCustomerDto(oidcUser),false);
                }
            }catch (Exception e){
                e.printStackTrace();
                if(e instanceof InvalidStateException) throw e;
                throw new AuthenticationServiceException("Couldn't complete authorization, try again after some time!");
            }

            return CustomerDetails.builder()
                    .authorities(oidcUser.getAuthorities())
                    .email(oidcUser.getAttribute("email"))
                    .name(oidcUser.getAttribute("name"))
                    .attributes(oidcUser.getAttributes())
                    .password(null)
                    .userId(oidcUser.getName()).build();
        };
    }

    private CustomerDto oidcUserToCustomerDto(OAuth2User oidcUser) {
        CustomerDto customerDto = new CustomerDto();
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        customerDto.setPhoneNumber("0987654321");
        customerDto.setEmail(oidcUser.getAttribute("email"));
        customerDto.setRole("OIDC_USER");
        customerDto.setUsername(oidcUser.getAttribute("name"));
        customerDto.setConfirmPassword(password);
        customerDto.setPassword(password);
        return customerDto;
    }
}
