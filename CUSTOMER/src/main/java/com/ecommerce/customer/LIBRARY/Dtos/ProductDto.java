package com.ecommerce.customer.LIBRARY.Dtos;

import com.ecommerce.customer.LIBRARY.Model.User.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter @ToString
public class ProductDto {

    Long id;

    @NotNull(message = "Product name cannot be NULL")
    private String name;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;

    @NotNull(message = "Cost price cannot be null")
    private double costPrice;

    @NotNull(message = "Sale price cannot be null")
    private double salePrice;

    @NotNull(message = "Description should be added")
    private String description;

    @NotNull(message = "Product manufacturer should be mentioned")
    private String brand;

    @NotNull(message = "Mention the size available")
    private List<String> size;

    @NotNull(message = "Include at-least 2 images")
    private List<MultipartFile> imagesUrls;

    @NotNull(message = "Mention the category")
    private Category category;

    private boolean isDeleted;
}
