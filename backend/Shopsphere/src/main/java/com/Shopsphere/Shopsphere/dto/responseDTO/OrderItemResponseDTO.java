package com.Shopsphere.Shopsphere.dto.responseDTO;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    private Integer quantity;
    private Double price;
}
