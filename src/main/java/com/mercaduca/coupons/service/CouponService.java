package com.mercaduca.coupons.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.coupons.dto.CouponDTOs;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CouponDTOs.CouponResponse createCoupon(CouponDTOs.CreateCouponRequest request, Long sellerId);
    CouponDTOs.CouponResponse validateCoupon(CouponDTOs.ValidateCouponRequest request);
    PageResponse<CouponDTOs.CouponResponse> getSellerCoupons(Long sellerId, Pageable pageable);
    void deactivateCoupon(Long couponId, Long sellerId);
    /** Elimina el cupon definitivamente de la base de datos. */
    void deleteCoupon(Long couponId, Long sellerId);
}
