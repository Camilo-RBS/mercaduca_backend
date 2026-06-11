package com.mercaduca.auth.service;

import com.mercaduca.auth.dto.AuthDTOs;

public interface AuthService {
    AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request);
    AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request);
    AuthDTOs.AuthResponse refreshToken(AuthDTOs.RefreshTokenRequest request);
}
