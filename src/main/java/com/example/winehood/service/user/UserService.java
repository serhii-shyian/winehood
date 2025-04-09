package com.example.winehood.service.user;

import com.example.winehood.dto.user.UserRegisterRequestDto;
import com.example.winehood.dto.user.UserResponseDto;
import com.example.winehood.exception.RegistrationException;
import com.example.winehood.model.User;
import java.util.List;

public interface UserService {
    UserResponseDto register(UserRegisterRequestDto registrationDto)
            throws RegistrationException;

    UserResponseDto updateUserRoles(Long userId, List<String> roleNames);

    UserResponseDto findProfile(User user);

    UserResponseDto updateProfile(User user, UserRegisterRequestDto updateDto);
}
