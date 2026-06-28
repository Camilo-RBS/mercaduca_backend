package com.mercaduca.auth.dto;

import com.mercaduca.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTOs {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        private String phoneNumber;

        // El rol es ignorado en el servicio — todo usuario nuevo es BUYER.
        // Se acepta para compatibilidad con el frontend pero no tiene efecto.
        private Role role;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Data
    public static class AuthResponse {
        private Long userId;
        private String username;
        private String email;
        private String role;
        private String accessToken;
        private String refreshToken;
        private long expiresIn;
    }

    @Data
    public static class SellerRegistrationRequest {
        @NotBlank(message = "Store name is required")
        @Size(max = 150)
        private String storeName;

        private String storeDescription;

        @NotBlank(message = "Tax ID is required")
        private String taxId;
    }
}
