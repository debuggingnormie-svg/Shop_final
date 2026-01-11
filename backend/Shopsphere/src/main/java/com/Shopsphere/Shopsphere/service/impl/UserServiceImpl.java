package com.Shopsphere.Shopsphere.service.impl;

import com.Shopsphere.Shopsphere.dto.requestDTO.UserAuthRequestDTO;
import com.Shopsphere.Shopsphere.dto.requestDTO.UserRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.UserAuthResponseDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.UserResponseDTO;
import com.Shopsphere.Shopsphere.entity.User;
import com.Shopsphere.Shopsphere.repository.UserRepository;
import com.Shopsphere.Shopsphere.security.JwtUtil;
import com.Shopsphere.Shopsphere.service.UserService;
import com.Shopsphere.Shopsphere.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserAuthResponseDTO register(UserRequestDTO userRequestDTO) {
        if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword())); // Ensure PasswordEncoder is configured
        user.setEmail(userRequestDTO.getEmail());
        user.setRole("USER"); // Default role
        user.setName(userRequestDTO.getName());
        user.setPhone(userRequestDTO.getPhone());
        user.setAddress(userRequestDTO.getAddress());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        UserAuthResponseDTO responseDTO = new UserAuthResponseDTO();
        responseDTO.setToken(token);
        responseDTO.setRole(user.getRole());
        responseDTO.setUserId(user.getId());
        return responseDTO;
    }

    @Override
    public UserAuthResponseDTO login(UserAuthRequestDTO userAuthRequestDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userAuthRequestDTO.getUsername(),
                            userAuthRequestDTO.getPassword()));
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByUsername(userAuthRequestDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        UserAuthResponseDTO responseDTO = new UserAuthResponseDTO();
        responseDTO.setToken(token);
        responseDTO.setRole(user.getRole());
        responseDTO.setUserId(user.getId());
        return responseDTO;
    }

    @Override
    public UserResponseDTO getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole());
        responseDTO.setName(user.getName());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setAddress(user.getAddress());

        return responseDTO;
    }

    @Override
    public UserResponseDTO updateProfile(UserRequestDTO userRequestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userRequestDTO.getName() != null)
            user.setName(userRequestDTO.getName());
        if (userRequestDTO.getPhone() != null)
            user.setPhone(userRequestDTO.getPhone());
        if (userRequestDTO.getAddress() != null)
            user.setAddress(userRequestDTO.getAddress());
        // Add other fields as necessary, e.g., email?

        userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole());
        responseDTO.setName(user.getName());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setAddress(user.getAddress());

        return responseDTO;
    }
}
