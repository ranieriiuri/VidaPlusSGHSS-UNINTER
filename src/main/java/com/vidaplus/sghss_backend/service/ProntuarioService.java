package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.CriarProntuarioRequest;
import com.vidaplus.sghss_backend.dto.ProntuarioDTO;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.repository.ProntuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final AuditLogService auditLogService;

    // Criar novo prontuário
    public ProntuarioDTO criarProntuario(CriarProntuarioRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar prontuários.");
        }

        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        Prontuario prontuario = Prontuario.builder()
                .registros(request.registros())
                .prescricoes(request.prescricoes())
                .paciente(paciente)
                .build();

        // Vincula médico automaticamente se quem cria é MEDICO
        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO) {
            prontuario.setMedico(usuarioLogado.getMedico());
        }

        paciente.getProntuarios().add(prontuario);
        Prontuario salvo = prontuarioRepository.save(prontuario);

        // Log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_PRONTUARIO",
                "Prontuario",
                salvo.getId(),
                "Paciente: " + paciente.getNome()
        );

        return ProntuarioDTO.from(salvo);
    }

    // Listar prontuários
    public List<ProntuarioDTO> listarProntuarios(Usuario usuarioLogado) {
        List<Prontuario> prontuarios;

        switch (usuarioLogado.getPerfil()) {
            case PACIENTE -> {
                Paciente paciente = pacienteRepository.findByUsuario(usuarioLogado)
                        .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));
                prontuarios = paciente.getProntuarios();
            }
            case MEDICO -> {
                prontuarios = prontuarioRepository.findByMedicoUsuarioId(usuarioLogado.getId());
            }
            case ADMIN -> {
                prontuarios = prontuarioRepository.findAll();
            }
            default -> throw new AccessDeniedException("Perfil desconhecido.");
        }

        return prontuarios.stream()
                .map(ProntuarioDTO::from)
                .toList();
    }

    // Buscar por ID
    public ProntuarioDTO buscarPorId(Long id, Usuario usuarioLogado) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !prontuario.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Pacientes só podem acessar seus próprios prontuários.");
        }

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !prontuario.getMedico().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem acessar prontuários de seus pacientes.");
        }

        return ProntuarioDTO.from(prontuario);
    }

    // Atualizar prontuário
    public ProntuarioDTO atualizarProntuario(Long id, CriarProntuarioRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para atualizar prontuários.");
        }

        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        // Se MEDICO, só pode atualizar seus pacientes
        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !prontuario.getMedico().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem atualizar prontuários de seus pacientes.");
        }

        prontuario.setRegistros(request.registros());
        prontuario.setPrescricoes(request.prescricoes());

        Prontuario salvo = prontuarioRepository.save(prontuario);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_PRONTUARIO",
                "Prontuario",
                salvo.getId(),
                "Paciente: " + salvo.getPaciente().getNome()
        );

        return ProntuarioDTO.from(salvo);
    }

    // Deletar prontuário
    public void deletarProntuario(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar prontuários.");
        }

        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        prontuario.getPaciente().getProntuarios().remove(prontuario);
        prontuarioRepository.delete(prontuario);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "DELETAR_PRONTUARIO",
                "Prontuario",
                prontuario.getId(),
                "Paciente: " + prontuario.getPaciente().getNome()
        );
    }
}
