package com.mercaduca.warnings.service;

import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.notifications.service.NotificationService;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import com.mercaduca.warnings.dto.WarningDTOs;
import com.mercaduca.warnings.entity.SellerWarning;
import com.mercaduca.warnings.repository.SellerWarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarningService {

    private final SellerWarningRepository warningRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public WarningDTOs.WarningResponse issueWarning(
            Long sellerId, Long adminId, WarningDTOs.IssueWarningRequest req) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        SellerWarning warning = SellerWarning.builder()
                .seller(seller).admin(admin).reason(req.getReason()).build();
        warningRepository.save(warning);

        notificationService.notifySellerWarning(seller, req.getReason());
        return toResponse(warning);
    }

    @Transactional(readOnly = true)
    public List<WarningDTOs.WarningResponse> getSellerWarnings(Long sellerId) {
        return warningRepository.findBySellerId(sellerId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long countWarnings(Long sellerId) {
        return warningRepository.countBySellerId(sellerId);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private WarningDTOs.WarningResponse toResponse(SellerWarning w) {
        WarningDTOs.WarningResponse r = new WarningDTOs.WarningResponse();
        r.setId(w.getId());
        r.setSellerId(w.getSeller().getId());
        r.setSellerName(w.getSeller().getFirstName() + " " + w.getSeller().getLastName());
        r.setAdminId(w.getAdmin().getId());
        r.setAdminName(w.getAdmin().getFirstName() + " " + w.getAdmin().getLastName());
        r.setReason(w.getReason());
        r.setAcknowledged(w.isAcknowledged());
        r.setCreatedAt(w.getCreatedAt());
        return r;
    }
}
