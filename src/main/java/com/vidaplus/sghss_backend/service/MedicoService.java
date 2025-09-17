package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.*;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditLogService auditLogService; // ← audit logs

    /**
     * Criar médico
     * Apenas ADMIN
     */
    public Medico criarMedico(Medico medico, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem criar médicos.");
        }

        if (medico.getUsuario() == null || medico.getUsuario().getId() == null) {
            throw new IllegalArgumentException("O médico deve ter um usuário existente (id obrigatório).");
        }

        Usuario usuario = usuarioRepository.findById(medico.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário associado não encontrado."));

        if (usuario.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new IllegalArgumentException("Não é possível associar um paciente como médico.");
        }

        if (medicoRepository.existsByUsuario(usuario)) {
            throw new IllegalArgumentException("Usuário já está associado a outro médico.");
        }

        Medico medicoParaSalvar = Medico.builder()
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .especialidade(medico.getEspecialidade())
                .usuario(usuario)
                .build();

        Medico salvo = medicoRepository.save(medicoParaSalvar);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_MEDICO",
                "Medico",
                salvo.getId(),
                "Nome: " + salvo.getNome() + ", CRM: " + salvo.getCrm()
        );

        return salvo;
    }

    public List<MedicoRespostaDTO> listarMedicos(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Pacientes não podem listar médicos.");
        }

        return medicoRepository.findAll()
                .stream()
                .map(medico -> MedicoRespostaDTO.builder()
                        .id(medico.getId())
                        .nome(medico.getNome())
                        .crm(medico.getCrm())
                        .especialidade(medico.getEspecialidade())
                        .usuarioId(medico.getUsuario() != null ? medico.getUsuario().getId() : null)
                        .agendaSlots(
                                medico.getAgendaSlots().stream()
                                        .map(slot -> AgendaMedicaSlotDTO.builder()
                                                .id(slot.getId())
                                                .data(slot.getData())
                                                .hora(slot.getHora())
                                                .disponivel(slot.isDisponivel())
                                                .build())
                                        .collect(Collectors.toList()) // <- aqui
                        )
                        .consultas(
                                medico.getConsultas().stream()
                                        .map(consulta -> ConsultaDTO.builder()
                                                .id(consulta.getId())
                                                .data(consulta.getData().toString())
                                                .hora(consulta.getHora().toString())
                                                .status(consulta.getStatus().name())
                                                .paciente(consulta.getPaciente() != null
                                                        ? new PacienteDTO(
                                                        consulta.getPaciente().getId(),
                                                        consulta.getPaciente().getNome(),
                                                        consulta.getPaciente().getCpf())
                                                        : null)
                                                .build())
                                        .collect(Collectors.toList()) // <- e aqui
                        )
                        .build())
                .collect(Collectors.toList()); // <- e aqui
    }


    public List<Medico> listarEntidadesMedicos(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Usuário não autorizado.");
        }
        return medicoRepository.findAll();
    }

    public List<Medico> listarTodosMedicos() {
        return medicoRepository.findAll();
    }

    public Medico buscarEntidadePorId(Long id, Usuario usuarioLogado) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode acessar seus próprios dados.");
        }

        return medico;
    }

    public MedicoDTO buscarPorId(Long id, Usuario usuarioLogado) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem acessar seus próprios dados.");
        }

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Pacientes não podem acessar médicos.");
        }

        return MedicoDTO.builder()
                .id(medico.getId())
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .especialidade(medico.getEspecialidade())
                .usuarioId(medico.getUsuario() != null ? medico.getUsuario().getId() : null)
                .build();
    }

    public Medico atualizarMedico(Long id, Medico medicoAtualizado, Usuario usuarioLogado) {
        Medico medicoExistente = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medicoExistente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem atualizar seus próprios dados.");
        }

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Pacientes não podem atualizar médicos.");
        }

        medicoExistente.setNome(medicoAtualizado.getNome());
        medicoExistente.setCrm(medicoAtualizado.getCrm());
        medicoExistente.setEspecialidade(medicoAtualizado.getEspecialidade());

        Medico salvo = medicoRepository.save(medicoExistente);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_MEDICO",
                "Medico",
                salvo.getId(),
                "Nome: " + salvo.getNome() + ", CRM: " + salvo.getCrm()
        );

        return salvo;
    }

    @Transactional
    public void deletarMedico(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar médicos.");
        }

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        Usuario usuario = medico.getUsuario();
        if (usuario != null) {
            usuario.setMedico(null);
            medico.setUsuario(null);
        }

        if (medico.getConsultas() != null) {
            medico.getConsultas().clear();
        }

        medicoRepository.delete(medico);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "DELETAR_MEDICO",
                "Medico",
                medico.getId(),
                "Nome: " + medico.getNome() + ", CRM: " + medico.getCrm()
        );
    }
}
