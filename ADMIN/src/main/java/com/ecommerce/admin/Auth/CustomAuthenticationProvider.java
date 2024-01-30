package com.ecommerce.admin.Auth;

import com.ecommerce.admin.LIBRARY.Exceptions.OtpInvalidException;
import com.ecommerce.admin.LIBRARY.Model.Utils.Otp;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.AdminRepository;
import com.ecommerce.admin.LIBRARY.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    AdminRepository adminRepository;
    OtpService otpService;

    @Autowired
    public CustomAuthenticationProvider(PasswordEncoder passwordEncoder,
                                        AdminRepository adminRepository,
                                        UserDetailsService userDetailsService,
                                        OtpService otpService) {
        super(passwordEncoder);
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
        this.adminRepository = adminRepository;
        this.otpService = otpService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        authentication = super.authenticate(authentication);

        if(adminRepository.existsAdminByUsername(authentication.getName())){
            try{
                Otp ServerOtp = otpService.findByUsername(authentication.getName());
                CustomAuthenticationDetails authenticationDetails = (CustomAuthenticationDetails)
                        authentication.getDetails();

                if(!Objects.equals(authenticationDetails.getOtp(), ServerOtp.getCode())){
                    throw new BadCredentialsException("Otp doesn't match");
                }

               if(!otpService.validateOtp(authenticationDetails.getOtp(), authentication.getName())){
                   throw new OtpInvalidException("Otp expired","Your otp is either invalid, expired or used.  Try generating a new one");
               }else{
                   ServerOtp.setUsed(true);
                   otpService.save(ServerOtp);
               }
            }
            catch (Exception e){
                throw new AuthenticationServiceException("Failed to generate server-side 2FA code");
            }
        }
        return authentication;
    }
}
