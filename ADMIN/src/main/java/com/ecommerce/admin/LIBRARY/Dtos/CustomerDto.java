package com.ecommerce.admin.LIBRARY.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    @NotEmpty(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Password cannot be null")
    @Size(min = 3, max = 12, message = "Password should be of minimum 8 characters")
    private String password;

    @NotNull(message = "Confirm password should be same as password and cannot be null")
    @Size(min = 3, max = 12, message = "Confirm Password should be of minimum 8 characters")
    private String confirmPassword;

    @NotNull(message = "Enter a Valid email") @Email(message = "Enter a valid email")
    private String email;

    @Size(min = 10, max = 10, message = "Phone number should be exactly 10 digits")
    @Pattern(regexp = "\\d{10}", message = "Enter a valid phone number of 10 digits.")
    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;

    String token;

    String role;

    public CustomerDto(String token) {
        this.token = token;
    }
}
