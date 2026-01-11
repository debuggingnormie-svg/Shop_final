package com.Shopsphere.Shopsphere.controller;

import com.Shopsphere.Shopsphere.dto.requestDTO.UserAuthRequestDTO;
import com.Shopsphere.Shopsphere.dto.requestDTO.UserRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.UserAuthResponseDTO;
import com.Shopsphere.Shopsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserAuthResponseDTO> register(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.register(userRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDTO> login(@RequestBody UserAuthRequestDTO userAuthRequestDTO) {
        return ResponseEntity.ok(userService.login(userAuthRequestDTO));
    }
}
