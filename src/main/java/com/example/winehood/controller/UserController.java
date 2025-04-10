package com.example.winehood.controller;

import com.example.winehood.dto.user.UserRegisterRequestDto;
import com.example.winehood.dto.user.UserResponseDto;
import com.example.winehood.dto.user.UserRoleUpdateRequestDto;
import com.example.winehood.model.User;
import com.example.winehood.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User management", description = "Endpoint for managing users")
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update role",
            description = "Updating user role, admins access only")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto updateUserRoles(
            @PathVariable Long id,
            @RequestBody @Valid UserRoleUpdateRequestDto userRoleUpdateDto) {
        return userService.updateUserRoles(id, userRoleUpdateDto.roleNames());
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get users profile",
            description = "Getting users profile if available")
    public UserResponseDto getProfile(@AuthenticationPrincipal User user) {
        return userService.findProfile(user);
    }

    @PutMapping(value = "/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update users profile",
            description = "Updating users profile if available")
    public UserResponseDto updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid UserRegisterRequestDto updateDto) {
        return userService.updateProfile(user, updateDto);
    }
}
