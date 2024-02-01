package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.Address;
import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {
    List<Address> findAddressByCustomer(Customer customer);

    @Query("select a from Address as a where a.customer.email =:customerEmail and a.isDefault")
    Address findDefaultAddressByCustomerEmail(String customerEmail);

    @Query("select count(a)>0 from Address as a where a.customer.email = :email and a.id = :id")
    boolean addressBelongsToCustomer(Long id, String email);

    @Query("update Address a set a.isDefault = false where a.customer.email = :email")
    @Modifying @Transactional
    void uncheckDefaultAddressForCustomer(String email);

    @Query("update Address a set a.isDefault = true where a.id = :id")
    @Modifying @Transactional
    void setAddressAsDefaultForCustomer(Long id);
}
