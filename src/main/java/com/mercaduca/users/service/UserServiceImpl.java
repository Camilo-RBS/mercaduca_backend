package com.mercaduca.users.service;

import com.mercaduca.auth.dto.AuthDTOs;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.common.enums.ProductStatus;
import com.mercaduca.common.enums.Role;
import com.mercaduca.common.enums.SellerStatus;
import com.mercaduca.orders.repository.OrderRepository;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.notifications.service.NotificationService;
import com.mercaduca.users.dto.UserDTOs;
import com.mercaduca.users.entity.SellerProfile;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.SellerProfileRepository;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDTOs.ProfileResponse getProfile(Long userId) {
        User user = findUser(userId);
        return toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserDTOs.ProfileResponse updateProfile(Long userId, UserDTOs.UpdateProfileRequest request) {
        User user = findUser(userId);
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());
        return toProfileResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(Long userId, UserDTOs.ChangePasswordRequest request) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("La contrasena actual es incorrecta");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTOs.SellerApprovalResponse registerAsSeller(AuthDTOs.SellerRegistrationRequest request, Long userId) {
        // Permitir re-solicitud si el perfil anterior fue RECHAZADO
        java.util.Optional<SellerProfile> existing = sellerProfileRepository.findByUserId(userId);
        if (existing.isPresent()) {
            SellerProfile profile = existing.get();
            if (profile.getStatus() == SellerStatus.REJECTED) {
                // Actualizar el perfil rechazado con los nuevos datos
                profile.setStoreName(request.getStoreName());
                profile.setStoreDescription(request.getStoreDescription());
                profile.setTaxId(request.getTaxId());
                profile.setStatus(SellerStatus.PENDING);
                profile.setRejectionReason(null);
                sellerProfileRepository.save(profile);
                return buildApprovalResponse(userId, request.getStoreName(), SellerStatus.PENDING,
                        "Solicitud re-enviada. En espera de verificacion.");
            }
            throw new BusinessException("Ya tienes una solicitud de vendedor con estado: " + profile.getStatus());
        }
        User user = findUser(userId);
        SellerProfile profile = SellerProfile.builder()
                .user(user).storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .taxId(request.getTaxId()).status(SellerStatus.PENDING).build();
        sellerProfileRepository.save(profile);
        return buildApprovalResponse(userId, request.getStoreName(), SellerStatus.PENDING,
                "Registro enviado. En espera de verificacion.");
    }

    @Override
    @Transactional
    public UserDTOs.SellerApprovalResponse approveSeller(Long sellerId) {
        SellerProfile profile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", "userId", sellerId));
        profile.setStatus(SellerStatus.VERIFIED);
        sellerProfileRepository.save(profile);

        // Actualizar el rol del usuario a SELLER para que tenga acceso completo
        User user = profile.getUser();
        user.setRole(Role.SELLER);
        userRepository.save(user);

        notificationService.notifySellerApproved(user);
        return buildApprovalResponse(sellerId, profile.getStoreName(), SellerStatus.VERIFIED, "Vendedor aprobado exitosamente");
    }

    @Override
    @Transactional
    public UserDTOs.SellerApprovalResponse rejectSeller(Long sellerId, String reason) {
        SellerProfile profile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", "userId", sellerId));
        profile.setStatus(SellerStatus.REJECTED);
        profile.setRejectionReason(reason);
        sellerProfileRepository.save(profile);
        notificationService.notifySellerRejected(profile.getUser(), reason);
        return buildApprovalResponse(sellerId, profile.getStoreName(), SellerStatus.REJECTED, "Vendedor rechazado");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDTOs.PendingSellerResponse> getPendingSellers(Pageable pageable) {
        Page<UserDTOs.PendingSellerResponse> page = sellerProfileRepository
                .findByStatus(SellerStatus.PENDING, pageable)
                .map(sp -> {
                    UserDTOs.PendingSellerResponse r = new UserDTOs.PendingSellerResponse();
                    r.setUserId(sp.getUser().getId());
                    r.setStoreName(sp.getStoreName());
                    r.setEmail(sp.getUser().getEmail());
                    r.setFirstName(sp.getUser().getFirstName());
                    r.setLastName(sp.getUser().getLastName());
                    r.setTaxId(sp.getTaxId());
                    r.setSubmittedAt(sp.getCreatedAt());
                    r.setStatus(SellerStatus.PENDING);
                    return r;
                });
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDTOs.AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<UserDTOs.AdminUserResponse> page = userRepository.findAll(pageable).map(u -> {
            UserDTOs.AdminUserResponse r = new UserDTOs.AdminUserResponse();
            r.setId(u.getId()); r.setUsername(u.getDisplayUsername()); r.setEmail(u.getEmail());
            r.setFirstName(u.getFirstName()); r.setLastName(u.getLastName());
            r.setRole(u.getRole()); r.setEnabled(u.isEnabled());
            r.setAccountNonLocked(u.isAccountNonLocked()); r.setCreatedAt(u.getCreatedAt());
            return r;
        });
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = findUser(userId);
        boolean nowEnabled = !user.isEnabled();
        user.setEnabled(nowEnabled);
        userRepository.save(user);
        if (nowEnabled) {
            notificationService.notifyAccountEnabled(user);
        } else {
            notificationService.notifyAccountDisabled(user);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTOs.UserSearchResult> searchUsers(String keyword, Long excludeId) {
        return userRepository.searchByKeyword(keyword, PageRequest.of(0, 20))
                .stream()
                .filter(u -> excludeId == null || !u.getId().equals(excludeId))
                .map(u -> {
                    UserDTOs.UserSearchResult r = new UserDTOs.UserSearchResult();
                    r.setId(u.getId());
                    r.setUsername(u.getDisplayUsername());
                    r.setFirstName(u.getFirstName());
                    r.setLastName(u.getLastName());
                    r.setRole(u.getRole());
                    return r;
                })
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────

    private UserDTOs.ProfileResponse toProfileResponse(User user) {
        UserDTOs.ProfileResponse r = new UserDTOs.ProfileResponse();
        r.setId(user.getId()); r.setUsername(user.getDisplayUsername()); r.setEmail(user.getEmail());
        r.setFirstName(user.getFirstName()); r.setLastName(user.getLastName());
        r.setPhoneNumber(user.getPhoneNumber()); r.setProfilePicture(user.getProfilePicture());
        r.setRole(user.getRole()); r.setEnabled(user.isEnabled()); r.setCreatedAt(user.getCreatedAt());
        sellerProfileRepository.findByUserId(user.getId()).ifPresent(sp -> {
            UserDTOs.SellerProfileInfo info = new UserDTOs.SellerProfileInfo();
            info.setId(sp.getId()); info.setStoreName(sp.getStoreName());
            info.setStoreDescription(sp.getStoreDescription()); info.setStatus(sp.getStatus());
            info.setAverageRating(sp.getAverageRating()); info.setTotalReviews(sp.getTotalReviews());
            info.setTotalSales(sp.getTotalSales()); info.setRejectionReason(sp.getRejectionReason());
            r.setSellerProfile(info);
        });
        return r;
    }

    @Override
    @Transactional
    public UserDTOs.SellerApprovalResponse suspendSeller(Long sellerId, String reason) {
        SellerProfile profile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", "userId", sellerId));
        profile.setStatus(SellerStatus.SUSPENDED);
        profile.setRejectionReason(reason);
        sellerProfileRepository.save(profile);
        notificationService.notifySellerSuspended(profile.getUser(), reason);
        return buildApprovalResponse(sellerId, profile.getStoreName(), SellerStatus.SUSPENDED,
                "Vendedor suspendido");
    }

    @Override
    @Transactional
    public UserDTOs.SellerApprovalResponse blockSeller(Long sellerId, String reason) {
        SellerProfile profile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", "userId", sellerId));
        profile.setStatus(SellerStatus.BLOCKED);
        profile.setRejectionReason(reason);
        sellerProfileRepository.save(profile);
        User user = profile.getUser();
        user.setAccountNonLocked(false);
        userRepository.save(user);
        notificationService.notifySellerBlocked(user, reason);
        return buildApprovalResponse(sellerId, profile.getStoreName(), SellerStatus.BLOCKED,
                "Vendedor bloqueado");
    }

    @Override
    @Transactional
    public UserDTOs.SellerApprovalResponse unblockSeller(Long sellerId) {
        SellerProfile profile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", "userId", sellerId));
        profile.setStatus(SellerStatus.VERIFIED);
        profile.setRejectionReason(null);
        sellerProfileRepository.save(profile);
        User user = profile.getUser();
        user.setAccountNonLocked(true);
        userRepository.save(user);
        return buildApprovalResponse(sellerId, profile.getStoreName(), SellerStatus.VERIFIED,
                "Vendedor desbloqueado");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDTOs.SellerAdminResponse> getAllSellers(Pageable pageable) {
        Page<UserDTOs.SellerAdminResponse> page = sellerProfileRepository
                .findAll(pageable).map(sp -> {
                    UserDTOs.SellerAdminResponse r = new UserDTOs.SellerAdminResponse();
                    r.setUserId(sp.getUser().getId());
                    r.setEmail(sp.getUser().getEmail());
                    r.setFirstName(sp.getUser().getFirstName());
                    r.setLastName(sp.getUser().getLastName());
                    r.setStoreName(sp.getStoreName());
                    r.setTaxId(sp.getTaxId());
                    r.setStatus(sp.getStatus());
                    r.setAverageRating(sp.getAverageRating());
                    r.setTotalReviews(sp.getTotalReviews());
                    r.setTotalSales(sp.getTotalSales());
                    r.setRejectionReason(sp.getRejectionReason());
                    r.setCreatedAt(sp.getCreatedAt());
                    // Stats adicionales
                    Long userId = sp.getUser().getId();
                    r.setTotalOrders(orderRepository.findOrdersBySellerId(
                            userId, org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements());
                    r.setActiveProducts(
                            productRepository.findBySellerIdAndStatus(userId, ProductStatus.ACTIVE).size());
                    java.math.BigDecimal rev = orderRepository.getTotalRevenueForSeller(userId);
                    r.setTotalRevenue(rev != null ? rev : java.math.BigDecimal.ZERO);
                    return r;
                });
        return PageResponse.from(page);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    private UserDTOs.SellerApprovalResponse buildApprovalResponse(Long id, String store, SellerStatus status, String msg) {
        UserDTOs.SellerApprovalResponse r = new UserDTOs.SellerApprovalResponse();
        r.setSellerId(id); r.setStoreName(store); r.setStatus(status); r.setMessage(msg);
        return r;
    }
}
