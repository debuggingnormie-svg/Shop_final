package com.Shopsphere.Shopsphere.dto.responseDTO;

import lombok.Data;

@Data
public class UserAuthResponseDTO {
    private String token;
    private String role;
    private Long userId;
}
