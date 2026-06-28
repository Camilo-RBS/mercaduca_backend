package com.mercaduca.address.entity;
import com.mercaduca.common.utils.BaseEntity;
import com.mercaduca.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "addresses")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Address extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false, length = 100) private String alias;
    @Column(name = "full_name", nullable = false, length = 150) private String fullName;
    @Column(nullable = false) private String street;
    @Column(nullable = false, length = 100) private String city;
    @Column(nullable = false, length = 100) private String state;
    @Column(nullable = false, length = 100) private String country;
    @Column(name = "zip_code", length = 20) private String zipCode;
    @Column(length = 20) private String phone;
    @Column(name = "is_default") @Builder.Default private boolean defaultAddress = false;
}
