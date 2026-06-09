package com.mercaduca.products.service;

import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.common.enums.ProductStatus;
import com.mercaduca.common.enums.Role;
import com.mercaduca.common.enums.SellerStatus;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.products.dto.ProductDTOs;
import com.mercaduca.products.entity.Category;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.mapper.ProductMapper;
import com.mercaduca.products.repository.CategoryRepository;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.products.specification.ProductSpecification;
import com.mercaduca.users.entity.SellerProfile;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.SellerProfileRepository;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDTOs.ProductResponse createProduct(ProductDTOs.CreateProductRequest request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));

        // Verificar que el vendedor tenga un perfil aprobado antes de publicar
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new BusinessException(
                        "Debes completar tu registro de tienda antes de publicar productos. " +
                        "Ve a tu perfil y envía la solicitud de vendedor."));
        if (sellerProfile.getStatus() != SellerStatus.VERIFIED) {
            throw new BusinessException(
                    "Tu cuenta de vendedor no está aprobada (estado: " + sellerProfile.getStatus() + "). " +
                    "Espera la aprobación del administrador.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setSeller(seller);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDTOs.ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.incrementViewCount(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDTOs.ProductResponse> searchProducts(
            ProductDTOs.ProductFilterRequest filter, Pageable pageable) {

        Sort sort = buildSort(filter.getSortBy(), filter.getSortDir());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Specification<Product> spec = Specification
                .where(ProductSpecification.hasStatus(ProductStatus.ACTIVE))
                .and(ProductSpecification.titleContains(filter.getKeyword()))
                .and(ProductSpecification.hasCategory(filter.getCategoryId()))
                .and(ProductSpecification.hasSeller(filter.getSellerId()))
                .and(ProductSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(ProductSpecification.isFeatured(filter.getFeatured()));

        Page<ProductDTOs.ProductResponse> page = productRepository
                .findAll(spec, sortedPageable)
                .map(productMapper::toResponse);

        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public ProductDTOs.ProductResponse updateProduct(Long productId,
                                                     ProductDTOs.UpdateProductRequest request, Long requestingUserId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Admins pueden editar cualquier producto; vendedores solo los suyos
        User requesting = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestingUserId));
        boolean isAdmin = requesting.getRole() == Role.ADMIN;
        if (!isAdmin && !product.getSeller().getId().equals(requestingUserId)) {
            throw new ForbiddenException("You can only edit your own products");
        }

        productMapper.updateEntityFromRequest(request, product);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("You can only delete your own products");
        }

        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDTOs.ProductResponse> getSellerProducts(Long sellerId, Pageable pageable) {
        Page<ProductDTOs.ProductResponse> page = productRepository
                .findBySellerIdAndStatus(sellerId, ProductStatus.ACTIVE, pageable)
                .map(productMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public void banProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setStatus(ProductStatus.BANNED);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void restoreProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String field = (sortBy != null) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}
