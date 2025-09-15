package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscar usuário pelo email (útil para login)
    Optional<Usuario> findByEmail(String email);
}