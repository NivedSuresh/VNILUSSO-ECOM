package com.ecommerce.customer.LIBRARY.ProductsService;

import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.Model.User.Wallet;

public interface WalletService {

    Wallet getWallet(String email);

    Wallet getWallet(Customer customer);

    Wallet saveWallet(Wallet wallet);

    Double getBalance(Customer customer);

    void initiateWalletRefund(Order order, Double refundAmount);
}
