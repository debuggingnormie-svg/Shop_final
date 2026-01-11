package com.Shopsphere.Shopsphere.dto.responseDTO;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;

    private Double price;
    private String description;
    private String imageUrl;
    private String categoryName;

    private Integer stockLevel;
    private Boolean isActive;
    private Integer reorderThreshold;
}
