package com.mercaduca.reviews.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.reviews.dto.ReviewDTOs;
import com.mercaduca.reviews.service.ReviewService;
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
@RequestMapping("/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reviews", description = "Product reviews and seller reputation")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    @Operation(summary = "Submit a product review (must have purchased the product)")
    public ResponseEntity<ApiResponse<ReviewDTOs.ReviewResponse>> createReview(
            @Valid @RequestBody ReviewDTOs.CreateReviewRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted", reviewService.createReview(request, user.getId())));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTOs.ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getProductReviews(productId, PageRequest.of(page, size))));
    }

    @PostMapping("/{reviewId}/response")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Add seller response to a review")
    public ResponseEntity<ApiResponse<ReviewDTOs.ReviewResponse>> addSellerResponse(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDTOs.SellerResponseRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Response added",
                reviewService.addSellerResponse(reviewId, request, user.getId())));
    }
}
