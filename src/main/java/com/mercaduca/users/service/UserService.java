package com.mercaduca.users.service;

import com.mercaduca.auth.dto.AuthDTOs;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.users.dto.UserDTOs;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDTOs.ProfileResponse getProfile(Long userId);
    UserDTOs.ProfileResponse updateProfile(Long userId, UserDTOs.UpdateProfileRequest request);
    void changePassword(Long userId, UserDTOs.ChangePasswordRequest request);
    UserDTOs.SellerApprovalResponse registerAsSeller(AuthDTOs.SellerRegistrationRequest request, Long userId);
    UserDTOs.SellerApprovalResponse approveSeller(Long sellerId);
    UserDTOs.SellerApprovalResponse rejectSeller(Long sellerId, String reason);
    UserDTOs.SellerApprovalResponse suspendSeller(Long sellerId, String reason);
    UserDTOs.SellerApprovalResponse blockSeller(Long sellerId, String reason);
    UserDTOs.SellerApprovalResponse unblockSeller(Long sellerId);
    PageResponse<UserDTOs.PendingSellerResponse> getPendingSellers(Pageable pageable);
    PageResponse<UserDTOs.SellerAdminResponse> getAllSellers(Pageable pageable);
    PageResponse<UserDTOs.AdminUserResponse> getAllUsers(Pageable pageable);
    void toggleUserStatus(Long userId);
    List<UserDTOs.UserSearchResult> searchUsers(String keyword, Long excludeId);
}
