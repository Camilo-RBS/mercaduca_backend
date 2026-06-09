package com.mercaduca.users.entity;

import com.mercaduca.common.enums.Role;
import com.mercaduca.common.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private Role role;

    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "is_account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    /**
     * UserDetails.getUsername() retorna email — necesario para que Spring Security
     * sea consistente con ApplicationConfig.userDetailsService() que hace findByEmail().
     * JWT subject = email → loadUserByUsername(email) → OK.
     * NOTA: Lombok omite generar getUsername() para el campo 'username' porque este
     * override ya existe. Por eso se expone getDisplayUsername() para acceder al
     * nombre de usuario real almacenado en BD.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Retorna el nombre de usuario real (campo 'username' de la BD).
     * Usar este método en DTOs para mostrar el username elegido por el usuario.
     */
    public String getDisplayUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
}
