package com.ecommerce.admin.LIBRARY.Dtos;

import jakarta.validation.constraints.*;
import lombok.*;


@Getter @ToString
@Setter @NoArgsConstructor @AllArgsConstructor
public class AddressDto {

    private Long id;

    @NotEmpty(message = "Recipient name cannot be blank")
    private String recipientName;

    @NotEmpty(message = "House name cannot be blank")
    private String houseName;

    @NotEmpty(message = "Address line cannot be blank")
    private String addressLine;

    @Pattern(regexp = "\\d{10}", message = "Phone number should be 10 digits")
    private String phoneNumber;

    @NotEmpty(message = "ZipCode cannot be empty")
    @Size(min = 6, max = 6, message = "Zipcode should be exactly 6 characters long")
    private String zipcode;

    @NotEmpty(message = "State cannot be blank")
    private String state;

    @NotEmpty(message = "District cannot be blank")
    private String district;

    @NotEmpty(message = "City cannot be blank")
    private String city;

    @NotEmpty(message = "Type of address cannot be blank")
    private String typeOfAddress;

    private String notes;

    private Boolean isDefault;

}

