package com.mercaduca.reviews.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.reviews.dto.ReviewDTOs;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDTOs.ReviewResponse createReview(ReviewDTOs.CreateReviewRequest request, Long buyerId);
    PageResponse<ReviewDTOs.ReviewResponse> getProductReviews(Long productId, Pageable pageable);
    ReviewDTOs.ReviewResponse addSellerResponse(Long reviewId, ReviewDTOs.SellerResponseRequest request, Long sellerId);
}
