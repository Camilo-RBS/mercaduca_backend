package com.mercaduca.reviews.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.orders.entity.Order;
import com.mercaduca.orders.repository.OrderItemRepository;
import com.mercaduca.orders.repository.OrderRepository;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.reviews.dto.ReviewDTOs;
import com.mercaduca.reviews.entity.Review;
import com.mercaduca.reviews.mapper.ReviewMapper;
import com.mercaduca.reviews.repository.ReviewRepository;
import com.mercaduca.users.entity.SellerProfile;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.SellerProfileRepository;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDTOs.ReviewResponse createReview(ReviewDTOs.CreateReviewRequest request, Long buyerId) {
        // Verificar que el comprador haya adquirido el producto (en cualquier orden)
        if (!orderItemRepository.existsByOrderBuyerIdAndProductId(buyerId, request.getProductId())) {
            throw new BusinessException("Solo puedes reseñar productos que hayas comprado");
        }
        // Prevenir duplicados: un comprador, un producto, una reseña
        if (reviewRepository.findByBuyerIdAndProductId(buyerId, request.getProductId()).isPresent()) {
            throw new BusinessException("Ya dejaste una reseña para este producto");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", buyerId));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // Obtener la orden si se proveyó; si no, buscar la primera orden del comprador con este producto
        Order order = null;
        if (request.getOrderId() != null) {
            order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));
        }

        Review review = Review.builder()
                .buyer(buyer)
                .product(product)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .verifiedPurchase(true)
                .build();

        reviewRepository.save(review);
        updateProductRating(product);
        updateSellerRating(product.getSeller().getId());

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewDTOs.ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Page<ReviewDTOs.ReviewResponse> page = reviewRepository
                .findByProductId(productId, pageable)
                .map(reviewMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public ReviewDTOs.ReviewResponse addSellerResponse(Long reviewId,
            ReviewDTOs.SellerResponseRequest request, Long sellerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getProduct().getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You can only respond to reviews on your own products");
        }
        if (review.getSellerResponse() != null) {
            throw new BusinessException("You have already responded to this review");
        }

        review.setSellerResponse(request.getResponse());
        reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void updateProductRating(Product product) {
        Double avg = reviewRepository.findAverageRatingByProductId(product.getId());
        long count = reviewRepository.countByProductId(product.getId());
        product.setAverageRating(avg != null ? avg : 0.0);
        product.setTotalReviews((int) count);
        productRepository.save(product);
    }

    private void updateSellerRating(Long sellerId) {
        sellerProfileRepository.findByUserId(sellerId).ifPresent(sp -> {
            Double avg = reviewRepository.findAverageRatingBySellerId(sellerId);
            sp.setAverageRating(avg != null ? avg : 0.0);
            sellerProfileRepository.save(sp);
        });
    }
}
