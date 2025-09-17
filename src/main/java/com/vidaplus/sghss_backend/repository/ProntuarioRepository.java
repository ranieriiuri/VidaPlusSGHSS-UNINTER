package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    // Buscar todos os prontuários de um paciente
    List<Prontuario> findByPaciente(Paciente paciente);

    // Buscar todos os prontuários de um paciente pelo usuário associado
    List<Prontuario> findByPacienteUsuarioId(Long usuarioId);

    // Opcional: buscar prontuários de um médico pelo usuário associado
    List<Prontuario> findByMedicoUsuarioId(Long usuarioId);
}
