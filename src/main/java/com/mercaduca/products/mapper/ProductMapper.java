package com.mercaduca.products.mapper;

import com.mercaduca.products.dto.ProductDTOs;
import com.mercaduca.products.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "totalSold", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "featured", ignore = true)
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "sku", ignore = true)
    Product toEntity(ProductDTOs.CreateProductRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerName",
            expression = "java(product.getSeller().getFirstName() + \" \" + product.getSeller().getLastName())")
    @Mapping(target = "sellerStoreName", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductDTOs.ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "totalSold", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "originalPrice", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProductDTOs.UpdateProductRequest request, @MappingTarget Product product);
}