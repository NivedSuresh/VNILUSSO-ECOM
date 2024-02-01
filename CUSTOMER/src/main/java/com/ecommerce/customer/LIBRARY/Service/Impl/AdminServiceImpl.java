package com.ecommerce.customer.LIBRARY.Service.Impl;

import com.ecommerce.customer.LIBRARY.Dtos.AdminDto;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidStateException;
import com.ecommerce.customer.LIBRARY.Exceptions.InvalidTokenException;
import com.ecommerce.customer.LIBRARY.Model.User.Admin;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.AdminRepository;
import com.ecommerce.customer.LIBRARY.Repository.UserRepos.RoleRepository;
import com.ecommerce.customer.LIBRARY.Repository.UtilRepos.TokenRepo;
import com.ecommerce.customer.LIBRARY.Service.AdminService;
import com.ecommerce.customer.LIBRARY.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminServiceImpl implements AdminService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final TokenRepo tokenRepo;

    private final TokenService tokenService;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, RoleRepository roleRepository,
                            BCryptPasswordEncoder passwordEncoder, TokenRepo tokenRepo,
                            TokenService tokenService) {
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
        this.tokenService = tokenService;
    }

    @Override
    public Admin save(AdminDto adminDto) {
        try{
            Admin admin = new Admin();
            admin.setFirstName(adminDto.getFirstName());
            admin.setLastName(adminDto.getLastName());
            admin.setUsername(adminDto.getUsername());
            admin.setPassword(adminDto.getPassword());
            admin.setRoles(Arrays.asList(roleRepository.findByRole("ADMIN")));

            return adminRepository.save(admin);
        }catch (NullPointerException N){throw new NullPointerException("Constraints error "+adminDto.toString());
    }}

    @Override
    public Admin findByUsername(String username) {
        try{return adminRepository.findByUsername(username);}
        catch (Exception e){e.printStackTrace();
            throw new InvalidStateException("", "Failed to fetch data, try again later!, l-56");
    }}

    @Override
    public boolean adminExists(String username) {
        try{return adminRepository.existsAdminByUsername(username);}
        catch (Exception e){e.printStackTrace();
            throw new InvalidStateException("", "Failed to fetch data, try again later!, l-66");
    }}

    @Override
    public void changePassword(String password, String username) {
        if(!tokenService.validateToken(tokenRepo.findByUsernameAndTokenFor(username, "PASSWORD_RESET"))){
            throw new InvalidTokenException("Token Exception","Token might have been used or is invalid");
        }
        tokenRepo.deleteByUsername(username);
        adminRepository.changePassword(passwordEncoder.encode(password), username);
    }
}
