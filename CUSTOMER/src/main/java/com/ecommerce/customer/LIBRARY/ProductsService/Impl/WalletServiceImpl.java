package com.ecommerce.customer.LIBRARY.ProductsService.Impl;

import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Model.User.Customer;
import com.ecommerce.customer.LIBRARY.Model.User.Order;
import com.ecommerce.customer.LIBRARY.Model.User.Wallet;
import com.ecommerce.customer.LIBRARY.ProductsService.WalletService;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CartRepo;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.CustomerRepository;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.WalletRepo;
import com.ecommerce.customer.LIBRARY.Service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletServiceImpl implements WalletService {

    WalletRepo walletRepo;
    CustomerService customerService;
    CustomerRepository customerRepository;
    CartRepo cartRepo;

    public WalletServiceImpl(WalletRepo walletRepo, CustomerService customerService,
                             CustomerRepository customerRepository, CartRepo cartRepo) {
        this.walletRepo = walletRepo;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.cartRepo = cartRepo;
    }

    @Override
    public Wallet getWallet(String email) {
        try{
            Wallet wallet = customerService.getWallet(email);
            if(wallet!=null) return wallet;
            else{
                Customer customer = customerService.findByEmail(email);
                customer.setWallet(walletRepo.save(new Wallet(null, 0.0, customer)));
                return customer.getWallet();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to fetch Wallet");
        }
    }

    @Override
    public Wallet getWallet(Customer customer) {
        return customer.getWallet()!=null?customer.getWallet():getWallet(customer.getEmail());
    }

    @Override
    public Wallet saveWallet(Wallet wallet) {
        try{return walletRepo.save(wallet);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to complete operation");
        }
    }

    @Override
    public Double getBalance(Customer customer) {
        return customer.getWallet()!=null?customer.getWallet().getBalance():0.0;
    }

    @Transactional
    @Override
    public void initiateWalletRefund(Order order, Double refundAmount) {
        try{
            Wallet wallet = getWallet(order.getCustomer());
            wallet.setBalance(wallet.getBalance()+refundAmount);
            walletRepo.save(wallet);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to Initiate wallet refund, contact us if Order was cancelled!");
        }
    }


}
