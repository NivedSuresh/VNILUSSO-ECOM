package com.ecommerce.customer.LIBRARY.Service.Impl;


import com.ecommerce.customer.LIBRARY.Exceptions.EmailNullException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidTokenException;
import com.ecommerce.customer.LIBRARY.Exceptions.OtpInvalidException;
import com.ecommerce.customer.LIBRARY.Model.Utils.Otp;
import com.ecommerce.customer.LIBRARY.Repository.UtilRepos.OtpRepo;
import com.ecommerce.customer.LIBRARY.Service.AdminService;
import com.ecommerce.customer.LIBRARY.Service.MailService;
import com.ecommerce.customer.LIBRARY.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    OtpRepo otpRepo;
    AdminService adminService;
    MailService mailService;

    @Autowired
    public OtpServiceImpl(OtpRepo otpRepo, AdminService adminService,
                          MailService mailService) {
        this.otpRepo = otpRepo;
        this.adminService = adminService;
        this.mailService = mailService;
    }

    public Otp findByUsername(String username){
        return otpRepo.findByUsername(username);
    }

    @Override
    public Otp generateOtp(String username) {

        try{
            if(username == null){
                throw new EmailNullException("email is null", "The page which you're try to access is Invalid or is removed.");
            }

            Otp otp = otpRepo.findByUsername(username);

            if(otp!=null && otp.getExpiration().isAfter(LocalDateTime.now())){
                if(otp.getUsed()){
                    throw new OtpInvalidException("Otp used", "An active otp exists, but is already used. Try again after sometime!");
                }
                return otp;
            }


            Duration duration = Duration.ofHours(0).plusMinutes(2);
            LocalDateTime expirationTime = LocalDateTime.now().plus(duration);

            if(otp==null){
                otp=new Otp();
                otp.setUsername(username);
            }

            String code = createOtp();
            otp.setCode(code);
            otp.setUsed(false);
            otp.setExpiration(expirationTime);

            mailService.sendMail(username, mailService.otpMailStructure(code));

            return otpRepo.save(otp);
        }catch (Exception e){
            if(e instanceof EmailNullException || e instanceof OtpInvalidException)
                throw e;
            throw new OtpInvalidException("", "Unable to generate OTP, try again after sometime.");
        }

    }

    private String createOtp(){
        StringBuilder otp= new StringBuilder();
        Random random = new Random();
        for(int i=0 ; i<6 ; i++){
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    public boolean validateOtp(String otp, String username){
        try{
            if(username==null)
                throw new EmailNullException("Email null", "The page you're looking for is Invalid");

            Otp dbOtp = otpRepo.findByUsername(username);
            if(otp==null || !Objects.equals(dbOtp.getCode(), otp) ||
                    dbOtp.getExpiration().isBefore(LocalDateTime.now())
                    || dbOtp.getUsed()){
                throw new OtpInvalidException("Invalid Otp", "Otp is invalid or might have expired");
            }
            return true;
        }catch (Exception e){
            if (e instanceof EmailNullException || e instanceof OtpInvalidException)
                throw e;
            throw new InvalidStateException("","Unable to complete operation, l-104");
        }

    }

    @Override
    public void setUsed(Otp otp) {
        try{
            if(otp!=null){
                otp.setUsed(true);
                save(otp);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to complete operation, l-113");
        }
    }

    @Override
    public boolean otpUsed(String username) {
        try{return otpRepo.isUsed(username);}
        catch (Exception e){
            throw new InvalidStateException("", "Unable to complete operation, l-124");
    }}

    @Override
    public Otp findByOtp(String otp) {
        try{return otpRepo.findByCode(otp);}
        catch (Exception e){
            throw new InvalidTokenException("","Unable to complete operation, l-133");
    }}

    @Override
    public void save(Otp otp) {
        otpRepo.save(otp);
    }
}
