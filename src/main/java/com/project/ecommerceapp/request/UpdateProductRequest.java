package com.project.ecommerceapp.request;

import com.project.ecommerceapp.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private Integer inventory;
    private String description;
    private Category category;
}
