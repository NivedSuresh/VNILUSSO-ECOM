package com.ecommerce.customer.LIBRARY.Service;


import com.ecommerce.customer.LIBRARY.Model.Utils.Otp;

public interface OtpService {

    Otp findByUsername(String username);
    Otp generateOtp(String username);
    boolean validateOtp(String otp, String username);

    void setUsed(Otp otp);

    boolean otpUsed(String username);

    Otp findByOtp(String otp);

    void save(Otp otp);
}
