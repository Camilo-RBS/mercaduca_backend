package com.mercaduca.config;

import com.mercaduca.common.enums.Role;
import com.mercaduca.users.entity.User;
import com.mercaduca.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inicializa datos esenciales al arrancar la aplicación.
 * Solo actúa si los datos no existen — es seguro correrlo múltiples veces.
 *
 * Crea:
 *   - Usuario admin (admin@gmail.com / 12345678)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (userRepository.findByEmail("admin@gmail.com").isPresent()) {
            log.info("Admin ya existe — omitiendo creación.");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .firstName("ad")
                .lastName("min")
                .role(Role.ADMIN)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(admin);
        log.info("✓ Admin creado: admin@gmail.com / 12345678");
    }
}
