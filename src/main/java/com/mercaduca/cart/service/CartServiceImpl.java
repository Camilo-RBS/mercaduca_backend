package com.mercaduca.cart.service;

import com.mercaduca.cart.dto.CartDTOs;
import com.mercaduca.cart.entity.CartItem;
import com.mercaduca.cart.repository.CartRepository;
import com.mercaduca.common.enums.ProductStatus;
import com.mercaduca.exceptions.custom.BusinessException;
import com.mercaduca.exceptions.custom.ForbiddenException;
import com.mercaduca.exceptions.custom.ResourceNotFoundException;
import com.mercaduca.products.entity.Product;
import com.mercaduca.products.repository.ProductRepository;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartDTOs.CartResponse getCart(Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        return buildCartResponse(items);
    }

    @Override
    @Transactional
    public CartDTOs.CartResponse addToCart(CartDTOs.AddToCartRequest request, Long userId) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessException("Product is not available");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("Requested quantity exceeds available stock ("
                    + product.getStock() + " left)");
        }
        if (product.getSeller().getId().equals(userId)) {
            throw new BusinessException("You cannot add your own product to the cart");
        }

        CartItem cartItem = cartRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    return CartItem.builder().user(user).product(product).quantity(0).build();
                });

        int newQty = cartItem.getQuantity() + request.getQuantity();
        if (product.getStock() < newQty) {
            throw new BusinessException("Total quantity exceeds available stock");
        }
        cartItem.setQuantity(newQty);
        cartRepository.save(cartItem);

        return buildCartResponse(cartRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public CartDTOs.CartResponse updateCartItem(Long itemId, CartDTOs.UpdateCartItemRequest request, Long userId) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));
        if (!item.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cart item does not belong to you");
        }
        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new BusinessException("Quantity exceeds stock");
        }
        item.setQuantity(request.getQuantity());
        cartRepository.save(item);
        return buildCartResponse(cartRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public CartDTOs.CartResponse removeFromCart(Long itemId, Long userId) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));
        if (!item.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cart item does not belong to you");
        }
        cartRepository.delete(item);
        return buildCartResponse(cartRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteAllByUserId(userId);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private CartDTOs.CartResponse buildCartResponse(List<CartItem> items) {
        List<CartDTOs.CartItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse).toList();

        BigDecimal total = itemResponses.stream()
                .map(CartDTOs.CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartDTOs.CartResponse response = new CartDTOs.CartResponse();
        response.setItems(itemResponses);
        response.setItemCount(items.size());
        response.setTotal(total);
        return response;
    }

    private CartDTOs.CartItemResponse toItemResponse(CartItem item) {
        CartDTOs.CartItemResponse r = new CartDTOs.CartItemResponse();
        r.setId(item.getId());
        r.setProductId(item.getProduct().getId());
        r.setProductTitle(item.getProduct().getTitle());
        r.setProductImage(item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0));
        r.setUnitPrice(item.getProduct().getPrice());
        r.setQuantity(item.getQuantity());
        r.setSubtotal(item.getItemTotal());
        r.setAvailableStock(item.getProduct().getStock());
        r.setSellerId(item.getProduct().getSeller().getId());
        r.setSellerName(item.getProduct().getSeller().getFirstName() + " " + item.getProduct().getSeller().getLastName());
        return r;
    }
}
