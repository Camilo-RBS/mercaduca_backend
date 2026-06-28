package com.mercaduca.users.dto;

import com.mercaduca.common.enums.Role;
import com.mercaduca.common.enums.SellerStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserDTOs {

    @Data
    public static class ProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String profilePicture;
        private Role role;
        private boolean enabled;
        private LocalDateTime createdAt;
        private SellerProfileInfo sellerProfile;
    }

    @Data
    public static class SellerProfileInfo {
        private Long id;
        private String storeName;
        private String storeDescription;
        private SellerStatus status;
        private Double averageRating;
        private Integer totalReviews;
        private Integer totalSales;
        private String rejectionReason;
    }

    @Data
    public static class UpdateProfileRequest {
        @Size(max = 80)
        private String firstName;
        @Size(max = 80)
        private String lastName;
        @Size(max = 20)
        private String phoneNumber;
        private String profilePicture;
    }

    @Data
    public static class ChangePasswordRequest {
        private String currentPassword;
        @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
        private String newPassword;
    }

    @Data
    public static class SellerApprovalResponse {
        private Long sellerId;
        private String storeName;
        private SellerStatus status;
        private String message;
    }

    @Data
    public static class PendingSellerResponse {
        private Long userId;
        private String storeName;
        private String email;
        private String firstName;
        private String lastName;
        private String taxId;
        private LocalDateTime submittedAt;
        private SellerStatus status;
    }

    @Data
    public static class AdminUserResponse {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Role role;
        private boolean enabled;
        private boolean accountNonLocked;
        private LocalDateTime createdAt;
    }

    /** Resultado de busqueda para el chat (cualquier usuario autenticado) */
    @Data
    public static class UserSearchResult {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private Role role;
    }

    /** Vista completa de un vendedor para el panel de administración */
    @Data
    public static class SellerAdminResponse {
        private Long userId;
        private String email;
        private String firstName;
        private String lastName;
        private String storeName;
        private String taxId;
        private SellerStatus status;
        private Double averageRating;
        private Integer totalReviews;
        private Integer totalSales;
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private Integer activeProducts;
        private LocalDateTime createdAt;
        private String rejectionReason;
    }
}
