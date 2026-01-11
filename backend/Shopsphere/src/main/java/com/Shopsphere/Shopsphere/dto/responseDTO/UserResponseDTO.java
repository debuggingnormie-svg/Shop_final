package com.Shopsphere.Shopsphere.dto.responseDTO;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String name;
    private String phone;
    private String address;
}
