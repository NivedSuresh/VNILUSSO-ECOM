package com.ecommerce.customer.LIBRARY.Service;

import com.ecommerce.customer.LIBRARY.Model.Utils.Token;

public interface TokenService {

    Token findByUsernameAndTokenFor(String username, String tokenFor);

    Token findByToken(String token);

    void generateTokenForPasswordReset(String username, boolean forgot);

    boolean validateToken(Token serverToken);

    boolean validateToken(Token serverToken, String email);

    void generateTokenAndSendMailForReferal(String principalEmail, String emailToBeReferred);

    void saveToken(Token token);
}
