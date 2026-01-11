package com.Shopsphere.Shopsphere.dto.requestDTO;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name; // title -> name

    private Double price;
    private String description;
    private String imageUrl;

    private Long categoryId;
    private String categoryName;

    private Integer stockLevel; // was stockQuantity
    private Boolean isActive;
    private Integer reorderThreshold;
}
