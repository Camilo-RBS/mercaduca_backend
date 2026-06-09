package com.mercaduca.products.specification;

import com.mercaduca.common.enums.ProductStatus;
import com.mercaduca.products.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) -> status != null
                ? cb.equal(root.get("status"), status)
                : cb.conjunction();
    }

    public static Specification<Product> titleContains(String keyword) {
        return (root, query, cb) -> keyword != null && !keyword.isBlank()
                ? cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%")
                : cb.conjunction();
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId != null
                ? cb.equal(root.get("category").get("id"), categoryId)
                : cb.conjunction();
    }

    public static Specification<Product> hasSeller(Long sellerId) {
        return (root, query, cb) -> sellerId != null
                ? cb.equal(root.get("seller").get("id"), sellerId)
                : cb.conjunction();
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Product> isFeatured(Boolean featured) {
        return (root, query, cb) -> featured != null && featured
                ? cb.isTrue(root.get("featured"))
                : cb.conjunction();
    }
}
