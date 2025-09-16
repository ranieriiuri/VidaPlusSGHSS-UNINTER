package com.vidaplus.sghss_backend.config;

import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository) {
        return args -> {
            String adminEmail = "ranieriiuri@teste.com";
            String adminSenha = "ranieri123";

            if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
                Usuario admin = Usuario.builder()
                        .email(adminEmail)
                        .senhaHash(passwordEncoder.encode(adminSenha))
                        .perfil("ADMIN")
                        .build();

                usuarioRepository.save(admin);
                System.out.println("Admin criado: " + adminEmail + " / senha: " + adminSenha);
            }
        };
    }
}
