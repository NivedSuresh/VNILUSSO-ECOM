package com.ecommerce.customer.LIBRARY.Service;

import com.ecommerce.customer.LIBRARY.Dtos.AdminDto;
import com.ecommerce.customer.LIBRARY.Model.User.Admin;

public interface AdminService {
    Admin save(AdminDto adminDto);
    Admin findByUsername(String username);

    boolean adminExists(String username);

    void changePassword(String password, String username);
}