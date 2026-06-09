package com.mercaduca.reviews.mapper;

import com.mercaduca.reviews.dto.ReviewDTOs;
import com.mercaduca.reviews.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productTitle", source = "product.title")
    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "buyerName",
             expression = "java(review.getBuyer().getFirstName() + \" \" + review.getBuyer().getLastName())")
    ReviewDTOs.ReviewResponse toResponse(Review review);
}
