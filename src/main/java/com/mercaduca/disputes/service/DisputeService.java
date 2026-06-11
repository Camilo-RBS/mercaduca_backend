package com.mercaduca.disputes.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.disputes.dto.DisputeDTOs;
import org.springframework.data.domain.Pageable;

public interface DisputeService {
    DisputeDTOs.DisputeResponse openDispute(Long orderId, DisputeDTOs.OpenDisputeRequest request, Long buyerId);
    PageResponse<DisputeDTOs.DisputeResponse> getMyDisputes(Long buyerId, Pageable pageable);
    PageResponse<DisputeDTOs.DisputeResponse> getSellerDisputes(Long sellerId, Pageable pageable);
    PageResponse<DisputeDTOs.DisputeResponse> getAllDisputes(Pageable pageable);
    DisputeDTOs.DisputeResponse resolveDispute(Long disputeId, DisputeDTOs.ResolveDisputeRequest request, Long adminId);
    DisputeDTOs.DisputeResponse sellerRespond(Long disputeId, DisputeDTOs.SellerResponseRequest request, Long sellerId);
    DisputeDTOs.DisputeResponse getDisputeById(Long id);
}
