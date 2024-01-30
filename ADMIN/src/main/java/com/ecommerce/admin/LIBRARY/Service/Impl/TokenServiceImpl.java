package com.ecommerce.admin.LIBRARY.Service.Impl;

import com.ecommerce.admin.LIBRARY.Exceptions.InvalidTokenException;
import com.ecommerce.admin.LIBRARY.Exceptions.TokenGenerationException;
import com.ecommerce.admin.LIBRARY.Model.Utils.Token;
import com.ecommerce.admin.LIBRARY.Repository.UtilRepos.TokenRepo;
import com.ecommerce.admin.LIBRARY.Service.MailService;
import com.ecommerce.admin.LIBRARY.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    TokenRepo tokenRepo;
    MailService mailService;

    @Autowired
    public TokenServiceImpl(TokenRepo tokenRepo, MailService mailService) {
        this.tokenRepo = tokenRepo;
        this.mailService = mailService;
    }

    @Override
    public Token findByUsernameAndTokenFor(String username, String tokenFor) {
        return tokenRepo.findByUsernameAndTokenFor(username, tokenFor);
    }

    @Override
    public Token findByToken(String token) {
        try{
            if(token == null)
                throw new NullPointerException("An error occurred");
            return tokenRepo.findByToken(token);
        }catch (Exception e){
            throw new InvalidTokenException("","An error occurred, token is either Null or Invalid.");
        }
    }

    @Override
    public void generateTokenForPasswordReset(String username, boolean forgot) {
        try{
            Duration duration = Duration.ofHours(0).plusMinutes(5);
            Token serverToken = generateToken(username, duration, "PASSWORD_RESET");
            mailService.sendMail(username, mailService.resetPasswordMailStructure(serverToken.getToken(), username, forgot));

        }catch (Exception e){
            if(e instanceof  InvalidTokenException)
                throw  e;
            throw new TokenGenerationException("", "Unable to generate token, try after sometime.");
        }
    }

    private Token generateToken(String username, Duration duration, String tokenFor) {
        Token serverToken = findByUsernameAndTokenFor(username, tokenFor);

        if(serverToken!=null && serverToken.getExpiration().isAfter(LocalDateTime.now())){
            if(serverToken.isUsed())
                throw new InvalidTokenException("Token was used", "A token already exists " +
                        "for this mail which was used, try again after some time.");
            return serverToken;
        }

        LocalDateTime expirationTime = LocalDateTime.now().plus(duration);

        if(serverToken == null){
            serverToken = new Token();
            serverToken.setUsername(username);
        }

        serverToken.setToken(UUID.randomUUID().toString());
        serverToken.setExpiration(expirationTime);
        serverToken.setUsed(false);
        serverToken.setTokenFor(tokenFor);
        return tokenRepo.save(serverToken);
    }

    @Override
    public boolean validateToken(Token token) {
        if(token==null || !tokenRepo.existsByToken(token.getToken()) || token.isUsed() || token.getExpiration().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Invalid", "Token is is either invalid or has expired, try again after sometime.");
        }
        return true;
    }

    @Override
    public boolean validateToken(Token serverToken, String email) {
        if(validateToken(serverToken) && Objects.equals(serverToken.getUsername(), email)){
            return true;
        }
        else throw new InvalidTokenException("","Token Invalid or expired");
    }

    @Override
    public void generateTokenAndSendMailForReferal(String principalEmail, String emailToBeReferred) {
        Duration duration = Duration.ofHours(24).plusMinutes(0);
        Token serverToken = generateToken(principalEmail, duration, "REFERRAL");
        mailService.sendMail(emailToBeReferred, mailService.referalMailStructure(principalEmail, serverToken.getToken()));
    }

    @Override
    public void saveToken(Token token) {
        try{tokenRepo.save(token);}
        catch (Exception e){e.printStackTrace();}
    }
}
