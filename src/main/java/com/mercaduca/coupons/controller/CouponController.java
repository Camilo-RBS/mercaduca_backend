package com.mercaduca.coupons.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.coupons.dto.CouponDTOs;
import com.mercaduca.coupons.service.CouponService;
import com.mercaduca.users.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Coupons", description = "Coupon and discount management")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create a coupon (Seller)")
    public ResponseEntity<ApiResponse<CouponDTOs.CouponResponse>> createCoupon(
            @Valid @RequestBody CouponDTOs.CreateCouponRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created",
                        couponService.createCoupon(request, user.getId())));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon code")
    public ResponseEntity<ApiResponse<CouponDTOs.CouponResponse>> validateCoupon(
            @Valid @RequestBody CouponDTOs.ValidateCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success(couponService.validateCoupon(request)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Get my coupons (Seller)")
    public ResponseEntity<ApiResponse<PageResponse<CouponDTOs.CouponResponse>>> getMyCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
                couponService.getSellerCoupons(user.getId(), PageRequest.of(page, size))));
    }

    /** Desactivacion logica (soft-delete): el cupon se mantiene en BD con active=false */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Deactivate a coupon (Seller)")
    public ResponseEntity<ApiResponse<Void>> deactivateCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        couponService.deactivateCoupon(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Coupon deactivated", null));
    }

    /** Eliminacion permanente (hard-delete): solo para cupones ya inactivos */
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Permanently delete an inactive coupon (Seller)")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        couponService.deleteCoupon(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted permanently", null));
    }
}
