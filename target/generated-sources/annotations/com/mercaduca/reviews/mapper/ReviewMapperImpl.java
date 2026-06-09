package com.mercaduca.reviews.mapper;

import com.mercaduca.products.entity.Product;
import com.mercaduca.reviews.dto.ReviewDTOs;
import com.mercaduca.reviews.entity.Review;
import com.mercaduca.users.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-08T19:19:15-0600",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public ReviewDTOs.ReviewResponse toResponse(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDTOs.ReviewResponse reviewResponse = new ReviewDTOs.ReviewResponse();

        reviewResponse.setProductId( reviewProductId( review ) );
        reviewResponse.setProductTitle( reviewProductTitle( review ) );
        reviewResponse.setBuyerId( reviewBuyerId( review ) );
        reviewResponse.setId( review.getId() );
        reviewResponse.setRating( review.getRating() );
        reviewResponse.setComment( review.getComment() );
        reviewResponse.setSellerResponse( review.getSellerResponse() );
        reviewResponse.setVerifiedPurchase( review.isVerifiedPurchase() );
        reviewResponse.setCreatedAt( review.getCreatedAt() );

        reviewResponse.setBuyerName( review.getBuyer().getFirstName() + " " + review.getBuyer().getLastName() );

        return reviewResponse;
    }

    private Long reviewProductId(Review review) {
        if ( review == null ) {
            return null;
        }
        Product product = review.getProduct();
        if ( product == null ) {
            return null;
        }
        Long id = product.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String reviewProductTitle(Review review) {
        if ( review == null ) {
            return null;
        }
        Product product = review.getProduct();
        if ( product == null ) {
            return null;
        }
        String title = product.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private Long reviewBuyerId(Review review) {
        if ( review == null ) {
            return null;
        }
        User buyer = review.getBuyer();
        if ( buyer == null ) {
            return null;
        }
        Long id = buyer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
