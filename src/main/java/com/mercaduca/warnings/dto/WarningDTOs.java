package com.mercaduca.warnings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class WarningDTOs {

    @Data
    public static class IssueWarningRequest {
        @NotBlank(message = "El motivo de la advertencia es requerido")
        @Size(max = 1000)
        private String reason;
    }

    @Data
    public static class WarningResponse {
        private Long id;
        private Long sellerId;
        private String sellerName;
        private Long adminId;
        private String adminName;
        private String reason;
        private boolean acknowledged;
        private LocalDateTime createdAt;
    }
}
