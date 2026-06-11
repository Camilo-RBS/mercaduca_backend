package com.mercaduca.auth.service;

import com.mercaduca.auth.dto.AuthDTOs;
import com.mercaduca.common.enums.Role;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.security.jwt.JwtService;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already taken: " + request.getUsername());
        }

        // Todo nuevo usuario es BUYER por defecto.
        // Para convertirse en SELLER debe solicitar aprobación al administrador.
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.BUYER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public AuthDTOs.AuthResponse refreshToken(AuthDTOs.RefreshTokenRequest request) {
        String userEmail = jwtService.extractUsername(request.getRefreshToken());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (!jwtService.isTokenValid(request.getRefreshToken(), user)) {
            throw new BusinessException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    private AuthDTOs.AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        AuthDTOs.AuthResponse response = new AuthDTOs.AuthResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getDisplayUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtService.getJwtExpiration());
        return response;
    }
}
