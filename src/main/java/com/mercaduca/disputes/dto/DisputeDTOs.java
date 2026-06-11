package com.mercaduca.disputes.dto;

import com.mercaduca.disputes.entity.Dispute;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class DisputeDTOs {

    @Data
    public static class OpenDisputeRequest {
        @NotBlank private String reason;
        @NotBlank private String description;
    }

    @Data
    public static class ResolveDisputeRequest {
        private Dispute.DisputeStatus status;
        private String adminNotes;
        private String resolution;
    }

    @Data
    public static class SellerResponseRequest {
        @NotBlank(message = "La respuesta no puede estar vacía")
        @Size(max = 2000)
        private String sellerResponse;

        @Size(max = 2000)
        private String sellerProposedSolution;
    }

    @Data
    public static class DisputeResponse {
        private Long id;
        private Long orderId;
        private String orderNumber;
        private Long buyerId;
        private String buyerName;
        private Long sellerId;
        private String sellerName;
        private String reason;
        private String description;
        private Dispute.DisputeStatus status;
        private String adminNotes;
        private String resolution;
        private String sellerResponse;
        private String sellerProposedSolution;
        private LocalDateTime createdAt;
    }
}
