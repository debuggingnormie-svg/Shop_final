package com.Shopsphere.Shopsphere.dto.requestDTO;

import lombok.Data;

@Data
public class CartItemRequestDTO {
    private Long productId;
    private Integer quantity;
}
