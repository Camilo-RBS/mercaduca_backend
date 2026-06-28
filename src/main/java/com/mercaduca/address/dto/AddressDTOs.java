package com.mercaduca.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
public class AddressDTOs {
    @Data public static class CreateAddressRequest {
        @NotBlank private String alias; @NotBlank private String fullName;
        @NotBlank private String street; @NotBlank private String city;
        @NotBlank private String state; @NotBlank private String country;
        private String zipCode; private String phone; private boolean defaultAddress;
    }
    @Data public static class AddressResponse {
        private Long id; private String alias; private String fullName;
        private String street; private String city; private String state;
        private String country; private String zipCode; private String phone;
        private boolean defaultAddress; private LocalDateTime createdAt;
    }
}
