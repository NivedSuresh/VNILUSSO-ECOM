package com.ecommerce.admin.Config;

import com.ecommerce.admin.Auth.CustomAuthenticationDetailsSource;
import com.ecommerce.admin.Handler.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AdminSecurityConfiguration {

    CustomSuccessHandler successHandler;
    CustomAuthenticationDetailsSource customAuthenticationDetailsSource;

    @Autowired
    public AdminSecurityConfiguration(CustomSuccessHandler successHandler,
                                      CustomAuthenticationDetailsSource customAuthenticationDetailsSource) {
        this.successHandler = successHandler;
        this.customAuthenticationDetailsSource = customAuthenticationDetailsSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf-> csrf
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/authenticate"))
        );

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/static/**","/css/**", "/fonts/**", "/imgs/**","/js/**","/sass/**", "/product-images").permitAll()
                .requestMatchers("/send-otp","/forgot-password", "/login",
                        "/send-token", "/reset-password").permitAll()
                .requestMatchers("/forgot-password").permitAll()
                .anyRequest().hasAuthority("ADMIN"));


        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/authenticate")
                .successHandler(successHandler)
                .authenticationDetailsSource(customAuthenticationDetailsSource));

        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .invalidSessionUrl("/login?expired")
                .maximumSessions(1)
        );

        return http.build();
    }
}
