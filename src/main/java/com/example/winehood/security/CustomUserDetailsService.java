package com.example.winehood.security;

import com.example.winehood.exception.EntityNotFoundException;
import com.example.winehood.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by username: " + username)
        );
    }
}
