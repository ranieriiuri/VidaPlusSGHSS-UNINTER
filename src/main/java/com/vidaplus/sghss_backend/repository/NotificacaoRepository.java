package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.Notificacao;
import com.vidaplus.sghss_backend.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByPacienteAndLidaFalse(Paciente paciente);
    List<Notificacao> findByPaciente(Paciente paciente);
}
