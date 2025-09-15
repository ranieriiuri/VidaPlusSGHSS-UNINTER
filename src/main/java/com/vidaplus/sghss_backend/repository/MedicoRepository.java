package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Buscar médico pelo CRM
    Optional<Medico> findByCrm(String crm);
}