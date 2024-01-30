package com.ecommerce.admin.LIBRARY.ProductsService;

public interface TaskOfferService {
    boolean isOfferEnabled(String offerName);
    void enabledOrDisable(String offerName, boolean enabled);
}
