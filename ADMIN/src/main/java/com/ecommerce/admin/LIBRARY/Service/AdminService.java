package com.ecommerce.admin.LIBRARY.Service;

import com.ecommerce.admin.LIBRARY.Dtos.AdminDto;
import com.ecommerce.admin.LIBRARY.Model.User.Admin;

public interface AdminService {
    Admin save(AdminDto adminDto);
    Admin findByUsername(String username);

    boolean adminExists(String username);

    void changePassword(String password, String username);
}