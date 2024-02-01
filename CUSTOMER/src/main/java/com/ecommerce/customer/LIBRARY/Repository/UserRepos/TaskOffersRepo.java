package com.ecommerce.customer.LIBRARY.Repository.UserRepos;

import com.ecommerce.customer.LIBRARY.Model.User.TaskOffers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TaskOffersRepo extends JpaRepository<TaskOffers, Long> {
    @Modifying @Transactional
    @Query("UPDATE TaskOffers as to set to.enabled = :enabled where to.offerName = :offerName")
    void enableOrDisable(String offerName, boolean enabled);

    @Query("select to.enabled from TaskOffers to where to.offerName = :offerName")
    boolean isOfferEnabled(String offerName);

}
