package com.mercaduca.products.controller;

import com.mercaduca.common.dto.ApiResponse;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.products.dto.ProductDTOs;
import com.mercaduca.products.service.ProductService;
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
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new product (Seller only)")
    public ResponseEntity<ApiResponse<ProductDTOs.ProductResponse>> createProduct(
            @Valid @RequestBody ProductDTOs.CreateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        ProductDTOs.ProductResponse response = productService.createProduct(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductDTOs.ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTOs.ProductResponse>>> searchProducts(
            @ModelAttribute ProductDTOs.ProductFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ProductDTOs.ProductResponse> response =
                productService.searchProducts(filter, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a product (Seller owner or Admin)")
    public ResponseEntity<ApiResponse<ProductDTOs.ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTOs.UpdateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        ProductDTOs.ProductResponse response = productService.updateProduct(id, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deactivate a product (Seller only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        productService.deleteProduct(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Product deactivated", null));
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get products by seller")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTOs.ProductResponse>>> getSellerProducts(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ProductDTOs.ProductResponse> response =
                productService.getSellerProducts(sellerId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Ban a product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> banProduct(@PathVariable Long id) {
        productService.banProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product banned", null));
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Restore a banned product to ACTIVE (Admin only)")
    public ResponseEntity<ApiResponse<Void>> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product restored", null));
    }
}
