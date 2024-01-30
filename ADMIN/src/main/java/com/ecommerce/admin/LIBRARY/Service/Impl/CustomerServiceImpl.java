package com.ecommerce.admin.LIBRARY.Service.Impl;

import com.ecommerce.admin.LIBRARY.Dtos.CustomerDto;
import com.ecommerce.admin.LIBRARY.Exceptions.CouponExpiredException;
import com.ecommerce.admin.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.admin.LIBRARY.Exceptions.InvalidTokenException;
import com.ecommerce.admin.LIBRARY.Model.User.Cart;
import com.ecommerce.admin.LIBRARY.Model.User.Customer;
import com.ecommerce.admin.LIBRARY.Model.User.Wallet;
import com.ecommerce.admin.LIBRARY.Model.User.Wishlist;
import com.ecommerce.admin.LIBRARY.Model.Utils.Token;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.AdminRepository;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.CustomerRepository;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.TaskOffersRepo;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.WalletRepo;
import com.ecommerce.admin.LIBRARY.Service.CustomerService;
import com.ecommerce.admin.LIBRARY.Service.OtpService;
import com.ecommerce.admin.LIBRARY.Service.TokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Date;
import java.util.Objects;

@Service
public class CustomerServiceImpl implements CustomerService {

    CustomerRepository customerRepository;
    PasswordEncoder passwordEncoder;
    OtpService otpService;
    TokenService tokenService;
    WalletRepo walletRepo;

    private final AdminRepository adminRepository;
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, OtpService otpService,
                               PasswordEncoder passwordEncoder, TokenService tokenService,
                               WalletRepo walletRepo, TaskOffersRepo taskOffersRepo, AdminRepository adminRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.tokenService = tokenService;
        this.walletRepo = walletRepo;
        this.adminRepository = adminRepository;
    }

    @Override
    public Customer findByEmail(String email) {
        try{return customerRepository.findByEmail(email);}
        catch (Exception e){throw new InvalidStateException("",
                "Couldn't fetch data from server!, l-47");
    }}

    @Override
    public boolean existsByEmail(String email) {
        try{return customerRepository.existsByEmail(email);}
        catch (Exception e){throw new InvalidStateException("",
                "Unable to check Customer Existence, l-53");
    }}

    @Override
    public boolean isBlocked(String email) {
        try{return customerRepository.isBlocked(email);}
        catch (Exception e){throw new InvalidStateException("",
                "Unable to fetch data from server, l-60");
    }}

    @Transactional
    @Override
    public void save(CustomerDto customerDto, boolean hashPasswordBeforeSaving) {
        try{
            Customer customer = customerRepository.findByEmail(customerDto.getEmail());

            if(customer==null){
                customer = new Customer();
                customer.setBlocked(false);
                customer.setDeleted(false);
                if(customerDto.getRole()==null){
                    customer.setRole("CUSTOMER");
                    customer.setPhoneNumber(customerDto.getPhoneNumber());
                }else{
                    customer.setRole(customerDto.getRole());
                    customer.setPhoneNumber(null);
                }
                customer.setCreatedOn(new Date());
            }

            customer.setEmail(customerDto.getEmail());
            customer.setUsername(customerDto.getUsername());

            if(Objects.equals(customer.getRole(), "CUSTOMER")){
                if(hashPasswordBeforeSaving) customer.setPassword(passwordEncoder.encode
                        (customerDto.getPassword()));
                else customer.setPassword(customerDto.getPassword());
            }
            customerRepository.save(customer);

            if(customerDto.getToken()!=null){
                Token token = tokenService.findByToken(customerDto.getToken());
                if(tokenService.validateToken(token)){
                    Wallet wallet = getWallet(token.getUsername());
                    wallet.setBalance(wallet.getBalance()+20);
                    walletRepo.save(wallet);
                    tokenService.saveToken(token);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to add user, l-100");
        }
    }

    @Override
    public void blockCustomer(Long id) {
        try{
            customerRepository.blockCustomer(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to Block Customer!, l-101");
        }
    }

    @Override
    public boolean existsById(Long id) {
        try{return customerRepository.existsById(id);}
        catch (Exception e){
            throw new InvalidStateException("", "Unable to check user existence, l-101");
    }}

    @Override
    public boolean isDeleted(String email) {
        try{return customerRepository.isDeleted(email);}
        catch (Exception e){
            throw new InvalidStateException("",
                    "Unable to check if customer account exists, l-111");
    }}

    @Override
    public void unBlockCustomer(Long id) {
        try{customerRepository.unblockUser(id);}
        catch (Exception e){throw new InvalidStateException("",
                "Unable to Unblock Customer, l-114");
    }}

    @Override
    public Cart findCart(String email) {
        try{return customerRepository.findCart(email);}
        catch (Exception e){throw new InvalidStateException("", "Unable to fetch Cart, l-124");
    }}

    @Override
    public CustomerDto getCustomerDto(String email) {
        try{
            Customer customer = customerRepository.findByEmail(email);
            if(customer==null){
                throw new InvalidStateException("","");
            }
            CustomerDto customerDto = new CustomerDto();
            customerDto.setEmail(customer.getEmail());
            customerDto.setPhoneNumber(customer.getPhoneNumber());
            customerDto.setUsername(customer.getUsername());

            return customerDto;
        }catch (Exception e){
            throw new InvalidStateException("","Couldn't find user, l-130");
        }
    }

    @Transactional
    @Override
    public boolean updateProfile(String email, String username, String phoneNumber,
                                 Principal principal, HttpSession session) {
        try{
            Customer customer = customerRepository.findByEmail(principal.getName());
            if(!Objects.equals(customer.getPhoneNumber(), phoneNumber)){
                customerRepository.updatePhoneNumber(customer.getEmail(), phoneNumber);
            }
            if(!Objects.equals(customer.getUsername(), username)){
                customerRepository.updateUserName(principal.getName(), username);
            }
            if(!Objects.equals(customer.getEmail(), email)){
                session.setAttribute("email", email);
                updateEmail(email, principal);
                return false;
            }
            return true;
        }catch (Exception e){
            throw new InvalidStateException("",
                    "Unable to update profile, try again later!, l-145");
    }}

    @Override
    public void updateEmail(String email, Principal principal) {
        try{if(!Objects.equals(principal.getName(), email))
                otpService.generateOtp(principal.getName());
        }catch (Exception e){throw new InvalidStateException("",
                "Unable to update email, try again later!, l-162");
    }}

    @Transactional
    @Override
    public void finishUpdateEmail(String newEmail, String currentEmail) {
        customerRepository.updateEmail(newEmail, currentEmail);
        otpService.setUsed(otpService.findByUsername(currentEmail));
    }

    @Override
    public void generateTokenForResetPassword(String email, boolean forgot) {
        try{
            if(!customerRepository.existsByEmail(email)){
                throw new InvalidStateException("", "Unable to find the page you're looking for.");
            }
            tokenService.generateTokenForPasswordReset(email, forgot);
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof InvalidStateException) throw e;
        }
    }

    @Transactional
    @Override
    public void resetPasswordValidateToken(String email, String tokenCode, String password) {
        try{
            Token token = tokenService.findByToken(tokenCode);

            if(token==null || !tokenService.validateToken(token) ||
                    !Objects.equals(token.getUsername(), email))
                throw new InvalidTokenException("", "Token is either used, expired or Invalid");

            customerRepository.changePassword(email, passwordEncoder.encode(password));
            token.setUsed(true);
            tokenService.saveToken(token);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("","Unable to complete operation, try again later");
        }
    }

    @Override
    public Wishlist getWishlist(String email) {
        try{
            return customerRepository.getWishlist(email);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("","Unable to fetch data from server, try again later!, l-203");
        }

    }

    @Override
    public Wallet getWallet(String email) {
        try{return customerRepository.getWallet(email);}
        catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Failed to fetch data, try again later!, l-205");
        }
    }

    @Override
    public Integer couponUsedCountByCustomer(Long customerId, Long couponId) {
        try{
            return customerRepository.couponUsedCountByCustomer(customerId, couponId);
        }
        catch (Exception e){e.printStackTrace();
            throw new InvalidStateException("", "Couldn't fetch coupon used count for the User");
    }}

    @Override
    public void referUser(String principalEmail, String emailToBeReferred, boolean offerEnabled) {
        try{
            if(!offerEnabled)
                throw new CouponExpiredException("Referral offer has been disabled for a while, we'll get back with it soon!");

            if(customerRepository.existsByEmail(emailToBeReferred) ||
                    adminRepository.existsAdminByUsername(emailToBeReferred))
                throw new InvalidStateException("CustomerAlreadyExists", "The Email which you're trying to refer already has an account with us!");

            tokenService.generateTokenAndSendMailForReferal(principalEmail, emailToBeReferred);
        }catch (Exception e){
            if(e instanceof InvalidStateException || e instanceof CouponExpiredException)
                throw e;
    }}

    @Override
    public String getCustomerAuthority(String email) {
        try{
            return customerRepository.getCustomerAuthority(email);
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidStateException("", "Unable to initiate operation, try again after some time!");
        }
    }

    @Transactional
    @Override
    public boolean updateIfOIDCUser(CustomerDto customerDto) {
        try{
            Customer customer = findByEmail(customerDto.getEmail());
            if(!Objects.equals(customer.getRole(), "OIDC_USER"))
                return false;
            customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
            customer.setPhoneNumber(customerDto.getPhoneNumber());
            customer.setRole("CUSTOMER");
            customerRepository.save(customer);
            return true;
        }catch (Exception e){
            throw new InvalidStateException("", "Couldn't complete login, try again later, l-300");
        }

    }

}
