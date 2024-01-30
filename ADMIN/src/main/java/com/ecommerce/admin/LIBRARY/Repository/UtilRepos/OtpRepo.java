package com.ecommerce.admin.LIBRARY.Repository.UtilRepos;

import com.ecommerce.admin.LIBRARY.Model.Utils.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepo extends JpaRepository<Otp, Long> {
    Otp findByUsername(String username);

    @Query("select o.used from Otp as o where o.username = :username")
    boolean isUsed(String username);

    Otp findByCode(String code);

}
