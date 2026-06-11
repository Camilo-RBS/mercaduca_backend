package com.mercaduca.coupons.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.coupons.dto.CouponDTOs;
import com.mercaduca.coupons.entity.Coupon;
import com.mercaduca.coupons.repository.CouponRepository;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CouponDTOs.CouponResponse createCoupon(CouponDTOs.CreateCouponRequest req, Long sellerId) {
        if (couponRepository.existsByCode(req.getCode())) {
            throw new BusinessException("Coupon code already exists: " + req.getCode());
        }
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));

        Coupon coupon = Coupon.builder()
                .code(req.getCode().toUpperCase())
                .description(req.getDescription())
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .minimumOrderAmount(req.getMinimumOrderAmount())
                .maximumDiscount(req.getMaximumDiscount())
                .seller(seller)
                .categoryId(req.getCategoryId())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .usageLimit(req.getUsageLimit())
                .build();

        return toResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTOs.CouponResponse validateCoupon(CouponDTOs.ValidateCouponRequest req) {
        Coupon coupon = couponRepository.findByCode(req.getCode().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", req.getCode()));
        return toResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CouponDTOs.CouponResponse> getSellerCoupons(Long sellerId, Pageable pageable) {
        Page<CouponDTOs.CouponResponse> page = couponRepository
                .findBySellerId(sellerId, pageable).map(this::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public void deactivateCoupon(Long couponId, Long sellerId) {
        Coupon coupon = findAndVerifyOwnership(couponId, sellerId);
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    /**
     * Elimina el cupon permanentemente.
     * Solo se permite si el cupon ya esta inactivo, para evitar
     * borrar accidentalmente cupones en uso.
     */
    @Override
    @Transactional
    public void deleteCoupon(Long couponId, Long sellerId) {
        Coupon coupon = findAndVerifyOwnership(couponId, sellerId);
        if (coupon.isActive()) {
            throw new BusinessException("Desactiva el cupon antes de eliminarlo definitivamente");
        }
        couponRepository.delete(coupon);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Coupon findAndVerifyOwnership(Long couponId, Long sellerId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", couponId));
        if (coupon.getSeller() == null || !coupon.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You can only manage your own coupons");
        }
        return coupon;
    }

    private CouponDTOs.CouponResponse toResponse(Coupon c) {
        CouponDTOs.CouponResponse r = new CouponDTOs.CouponResponse();
        r.setId(c.getId());
        r.setCode(c.getCode());
        r.setDescription(c.getDescription());
        r.setDiscountType(c.getDiscountType());
        r.setDiscountValue(c.getDiscountValue());
        r.setMinimumOrderAmount(c.getMinimumOrderAmount());
        r.setMaximumDiscount(c.getMaximumDiscount());
        r.setStartDate(c.getStartDate());
        r.setEndDate(c.getEndDate());
        r.setUsageLimit(c.getUsageLimit());
        r.setUsageCount(c.getUsageCount());
        r.setActive(c.isActive());
        r.setValid(c.isValid());
        return r;
    }
}
