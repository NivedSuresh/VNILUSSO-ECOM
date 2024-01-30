package com.ecommerce.admin.Security;

import com.ecommerce.admin.LIBRARY.Model.User.Admin;
import com.ecommerce.admin.LIBRARY.Repository.UserRepos.AdminRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@NoArgsConstructor
@Service
public class AdminDetailsService implements UserDetailsService {

    AdminRepository adminRepository;

    @Autowired
    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Admin profile = adminRepository.findByUsername(username);
        if(profile == null){
            throw new UsernameNotFoundException("Could not verify as User " +
                    "details was not available.");
        }else{

            return new User(
                    profile.getUsername(), profile.getPassword(),
                    profile.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(role.getRole()))
                            .collect(Collectors.toList()));
        }

    }
}
