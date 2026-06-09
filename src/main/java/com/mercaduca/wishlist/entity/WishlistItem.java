package com.mercaduca.wishlist.entity;
import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.products.entity.Product;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "wishlist_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","product_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class WishlistItem extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "product_id", nullable = false) private Product product;
}
