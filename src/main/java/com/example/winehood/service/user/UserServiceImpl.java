package com.example.winehood.service.user;

import com.example.winehood.dto.user.UserRegisterRequestDto;
import com.example.winehood.dto.user.UserResponseDto;
import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.exception.RegistrationException;
import com.example.winehood.mapper.UserMapper;
import com.example.winehood.model.Role;
import com.example.winehood.model.User;
import com.example.winehood.repository.role.RoleRepository;
import com.example.winehood.repository.user.UserRepository;
import com.example.winehood.service.shoppingcart.ShoppingCartService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegisterRequestDto registrationDto)
            throws RegistrationException {
        if (userRepository.findByUsername(registrationDto.username()).isPresent()) {
            throw new RegistrationException(
                    String.format("User with username %s already exists",
                            registrationDto.username()));
        }

        User userFromDto = userMapper.toEntity(registrationDto);
        userFromDto.setPassword(passwordEncoder.encode(registrationDto.password()));
        userFromDto.setRoles(findByNameContaining(Set.of(
                Role.RoleName.USER)));

        User savedUser = userRepository.save(userFromDto);
        shoppingCartService.createShoppingCart(userFromDto);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto updateUserRoles(Long userId, List<String> roleNames) {
        User user = getUser(userId);

        Set<Role.RoleName> roleEnums = roleNames.stream()
                .map(roleName -> Role.RoleName.valueOf(roleName.toUpperCase()))
                .collect(Collectors.toSet());

        Set<Role> roles = roleRepository.findAllByNameContaining(roleEnums);
        if (roles.isEmpty()) {
            throw new EntityNotFoundException("No matching roles found for the provided names.");
        }

        user.setRoles(roles);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto findProfile(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfile(User user, UserRegisterRequestDto updateDto) {
        getUser(user.getId());

        userMapper.updateEntityFromDto(user, updateDto);

        user.setPassword(passwordEncoder.encode(updateDto.password()));
        user.setRoles(user.getRoles());

        return userMapper.toDto(userRepository.save(user));
    }

    private Set<Role> findByNameContaining(Set<Role.RoleName> rolesSet) {
        return new HashSet<>(roleRepository.findAllByNameContaining(rolesSet));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with ID: " + userId));
    }
}
