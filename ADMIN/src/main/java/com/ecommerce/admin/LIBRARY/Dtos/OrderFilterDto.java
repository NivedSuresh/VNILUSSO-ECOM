package com.ecommerce.admin.LIBRARY.Dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class OrderFilterDto {
    String status;
    String sort;
    String paymentMethod;
}
