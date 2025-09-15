package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
    // Buscar prontuário por paciente
    Optional<Prontuario> findByPaciente(Paciente paciente);
    Optional<Prontuario> findByPacienteUsuario(Usuario usuario);
}