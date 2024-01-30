package com.ecommerce.admin.LIBRARY.Dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class FilterDto {
    List<String> category;
    Double min;
    Double max;
    List<String> sizes;
}
