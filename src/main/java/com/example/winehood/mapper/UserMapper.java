package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.user.UserRegisterRequestDto;
import com.example.winehood.dto.user.UserResponseDto;
import com.example.winehood.model.Role;
import com.example.winehood.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringSet")
    UserResponseDto toDto(User user);

    User toEntity(UserRegisterRequestDto registrationDto);

    void updateEntityFromDto(@MappingTarget User user, UserRegisterRequestDto updateDto);

    @Named("rolesToStringSet")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
