package com.nikhil.IntelliStock.service;

import com.nikhil.IntelliStock.config.JwtUtils;
import com.nikhil.IntelliStock.dto.AuthRequestDTO;
import com.nikhil.IntelliStock.dto.AuthResponseDTO;
import com.nikhil.IntelliStock.entity.Role;
import com.nikhil.IntelliStock.entity.User;
import com.nikhil.IntelliStock.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // Public registration — always defaults to standard STAFF
    public AuthResponseDTO register(AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        User user = new User(
                null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_STAFF
        );

        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponseDTO(token, user.getUsername(), user.getRole().name());
    }

    // Secured method — provisions a new user with ROLE_ADMIN
    public AuthResponseDTO createAdminUser(AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        User adminUser = new User(
                null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_ADMIN
        );

        userRepository.save(adminUser);
        String token = jwtUtils.generateToken(adminUser.getUsername(), adminUser.getRole().name());
        return new AuthResponseDTO(token, adminUser.getUsername(), adminUser.getRole().name());
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponseDTO(token, user.getUsername(), user.getRole().name());
    }
}