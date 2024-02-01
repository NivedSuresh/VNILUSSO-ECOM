package com.ecommerce.customer.LIBRARY.Repository.UtilRepos;

import com.ecommerce.customer.LIBRARY.Model.Utils.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {


    Token findByUsernameAndTokenFor(String username, String tokenFor);
    Token findByToken(String token);

    @Transactional
    @Modifying
    void deleteByUsername(String username);
    boolean existsByToken(String token);
}
