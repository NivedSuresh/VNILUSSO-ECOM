package com.ecommerce.customer.LIBRARY.Dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto {
    @Size(min = 3, max = 10, message = "First name contains 3-10 characters")
    private String firstName;
    @Size(min = 3, max = 10, message = "Last name contains 3-10 characters")
    private String lastName;
    @NotNull(message = "username should not be null")
    @Size(min = 5, max = 30, message = "Email should consist at-least 5-50 characters")
    private String username;
    @Size(min = 5, max = 10, message = "Password should contain at-least 5-10 characters")
    @NotNull(message = "password should not be null")
    private String password;
    private String repeatPassword;
}
