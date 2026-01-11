package com.Shopsphere.Shopsphere.controller;

import com.Shopsphere.Shopsphere.dto.responseDTO.UserResponseDTO;
import com.Shopsphere.Shopsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestBody com.Shopsphere.Shopsphere.dto.requestDTO.UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.updateProfile(userRequestDTO));
    }
}
