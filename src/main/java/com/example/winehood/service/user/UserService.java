package com.example.winehood.service.user;

import com.example.winehood.dto.user.UserRegisterRequestDto;
import com.example.winehood.dto.user.UserResponseDto;
import com.example.winehood.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegisterRequestDto registrationDto)
            throws RegistrationException;
}
