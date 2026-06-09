package com.mercaduca.wishlist.service;
import com.mercaduca.common.dto.PageResponse;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import com.mercaduca.wishlist.dto.WishlistDTOs;
import com.mercaduca.wishlist.entity.WishlistItem;
import com.mercaduca.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    @Override @Transactional
    public WishlistDTOs.WishlistItemResponse addToWishlist(Long productId, Long userId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId))
            throw new BusinessException("El producto ya está en tu lista de deseos");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productId));
        WishlistItem item = WishlistItem.builder().user(user).product(product).build();
        return toResponse(wishlistRepository.save(item));
    }
    @Override @Transactional
    public void removeFromWishlist(Long productId, Long userId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId); }
    @Override @Transactional(readOnly = true)
    public PageResponse<WishlistDTOs.WishlistItemResponse> getWishlist(Long userId, Pageable pageable) {
        Page<WishlistDTOs.WishlistItemResponse> page = wishlistRepository.findByUserId(userId, pageable).map(this::toResponse);
        return PageResponse.from(page);
    }
    @Override @Transactional(readOnly = true)
    public boolean isInWishlist(Long productId, Long userId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId); }
    private WishlistDTOs.WishlistItemResponse toResponse(WishlistItem item) {
        WishlistDTOs.WishlistItemResponse r = new WishlistDTOs.WishlistItemResponse();
        r.setId(item.getId()); r.setProductId(item.getProduct().getId());
        r.setProductTitle(item.getProduct().getTitle());
        r.setProductImage(item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0));
        r.setPrice(item.getProduct().getPrice()); r.setStatus(item.getProduct().getStatus().name());
        r.setAverageRating(item.getProduct().getAverageRating()); r.setAddedAt(item.getCreatedAt());
        return r;
    }
}
