package com.mercaduca.products.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.products.dto.ProductDTOs;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTOs.ProductResponse createProduct(ProductDTOs.CreateProductRequest request, Long sellerId);
    ProductDTOs.ProductResponse getProductById(Long id);
    PageResponse<ProductDTOs.ProductResponse> searchProducts(ProductDTOs.ProductFilterRequest filter, Pageable pageable);
    ProductDTOs.ProductResponse updateProduct(Long productId, ProductDTOs.UpdateProductRequest request, Long sellerId);
    void deleteProduct(Long productId, Long sellerId);
    PageResponse<ProductDTOs.ProductResponse> getSellerProducts(Long sellerId, Pageable pageable);
    void banProduct(Long productId);
    void restoreProduct(Long productId);
}
