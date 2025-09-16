package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.ConsultaRepository;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final AuditLogService auditLogService; // ← audit logs

    /**
     * Criar nova consulta
     * ADMIN ou MEDICO podem criar
     */
    public Consulta criarConsulta(Consulta consulta, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar consultas.");
        }

        Paciente paciente = pacienteRepository.findById(consulta.getPaciente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));
        consulta.setPaciente(paciente);

        Medico medico = medicoRepository.findById(consulta.getMedico().getId())
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));
        consulta.setMedico(medico);

        Consulta salvo = consultaRepository.save(consulta);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_CONSULTA",
                "Consulta",
                salvo.getId(),
                "Paciente: " + paciente.getNome() + ", Médico: " + medico.getNome()
        );

        return salvo;
    }

    /**
     * Listar consultas
     */
    public List<Consulta> listarConsultas(Usuario usuarioLogado) {
        return switch (usuarioLogado.getPerfil()) {
            case ADMIN -> consultaRepository.findAll();
            case MEDICO -> consultaRepository.findByMedicoUsuario(usuarioLogado);
            case PACIENTE -> consultaRepository.findByPacienteUsuario(usuarioLogado);
            default -> throw new AccessDeniedException("Perfil desconhecido.");
        };
    }

    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    /**
     * Buscar consulta por ID
     */
    public Consulta buscarPorId(Long id, Usuario usuarioLogado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        switch (usuarioLogado.getPerfil()) {
            case MEDICO -> {
                if (!consulta.getMedico().getUsuario().getId().equals(usuarioLogado.getId())) {
                    throw new AccessDeniedException("Médicos só podem acessar suas próprias consultas.");
                }
            }
            case PACIENTE -> {
                if (!consulta.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
                    throw new AccessDeniedException("Pacientes só podem acessar suas próprias consultas.");
                }
            }
            case ADMIN -> { /* acesso liberado */ }
            default -> throw new AccessDeniedException("Perfil desconhecido.");
        }

        return consulta;
    }

    /**
     * Atualizar consulta
     * Apenas ADMIN pode alterar
     */
    public Consulta atualizarConsulta(Long id, Consulta consultaAtualizada, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem atualizar consultas.");
        }

        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        consultaExistente.setData(consultaAtualizada.getData());
        consultaExistente.setHora(consultaAtualizada.getHora());
        consultaExistente.setStatus(consultaAtualizada.getStatus());

        Consulta salvo = consultaRepository.save(consultaExistente);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_CONSULTA",
                "Consulta",
                salvo.getId(),
                "Paciente: " + salvo.getPaciente().getNome() + ", Médico: " + salvo.getMedico().getNome()
        );

        return salvo;
    }

    /**
     * Deletar consulta
     * Apenas ADMIN
     */
    public void deletarConsulta(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar consultas.");
        }

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        consultaRepository.delete(consulta);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "DELETAR_CONSULTA",
                "Consulta",
                consulta.getId(),
                "Paciente: " + consulta.getPaciente().getNome() + ", Médico: " + consulta.getMedico().getNome()
        );
    }
}
