package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Buscar médico pelo CRM
    Optional<Medico> findByCrm(String crm);
    Optional<Medico> findByUsuario(Usuario usuario);
    boolean existsByUsuario(Usuario usuario);
}