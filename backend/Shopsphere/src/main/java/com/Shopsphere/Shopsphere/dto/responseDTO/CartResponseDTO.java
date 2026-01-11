package com.Shopsphere.Shopsphere.dto.responseDTO;

import lombok.Data;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private Double totalAmount;
    private List<CartItemResponseDTO> items;
}
