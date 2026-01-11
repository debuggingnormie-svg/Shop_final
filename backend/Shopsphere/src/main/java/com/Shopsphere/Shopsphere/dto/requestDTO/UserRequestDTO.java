package com.Shopsphere.Shopsphere.dto.requestDTO;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String role;
    private String name;
    private String phone;
    private String address;
}
