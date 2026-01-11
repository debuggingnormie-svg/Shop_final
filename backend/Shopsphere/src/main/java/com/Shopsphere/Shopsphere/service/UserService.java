package com.Shopsphere.Shopsphere.service;

import com.Shopsphere.Shopsphere.dto.requestDTO.UserAuthRequestDTO;
import com.Shopsphere.Shopsphere.dto.requestDTO.UserRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.UserAuthResponseDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.UserResponseDTO;

public interface UserService {
    UserAuthResponseDTO register(UserRequestDTO userRequestDTO);

    UserAuthResponseDTO login(UserAuthRequestDTO userAuthRequestDTO);

    UserResponseDTO getProfile();

    UserResponseDTO updateProfile(UserRequestDTO userRequestDTO);
}
