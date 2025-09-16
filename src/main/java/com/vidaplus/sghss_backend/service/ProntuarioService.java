package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.CriarProntuarioRequest;
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
    private final AuditLogService auditLogService; // ← audit logs

    /**
     * Criar novo prontuário
     * Apenas ADMIN ou MEDICO podem criar
     */
    public Prontuario criarProntuario(Prontuario prontuario, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar prontuários.");
        }

        Paciente paciente = pacienteRepository.findById(prontuario.getPaciente().getId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        prontuario.setPaciente(paciente);
        paciente.getProntuarios().add(prontuario);

        Prontuario salvo = prontuarioRepository.save(prontuario);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_PRONTUARIO",
                "Prontuario",
                salvo.getId(),
                "Paciente: " + paciente.getNome()
        );

        return salvo;
    }

    public List<Prontuario> listarProntuarios(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            Paciente paciente = pacienteRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));
            return paciente.getProntuarios();
        }
        return prontuarioRepository.findAll();
    }

    public List<Prontuario> listarTodosProntuarios() {
        return prontuarioRepository.findAll();
    }

    public Prontuario buscarPorId(Long id, Usuario usuarioLogado) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !prontuario.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Pacientes só podem acessar seus próprios prontuários.");
        }

        return prontuario;
    }

    public Prontuario atualizarProntuario(Long id, CriarProntuarioRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para atualizar prontuários.");
        }

        Prontuario prontuarioExistente = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        prontuarioExistente.setRegistros(request.registros());
        prontuarioExistente.setPrescricoes(request.prescricoes());

        Prontuario salvo = prontuarioRepository.save(prontuarioExistente);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_PRONTUARIO",
                "Prontuario",
                salvo.getId(),
                "Paciente: " + salvo.getPaciente().getNome()
        );

        return salvo;
    }

    public void deletarProntuario(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar prontuários.");
        }

        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        prontuario.getPaciente().getProntuarios().remove(prontuario);
        prontuarioRepository.delete(prontuario);

        // Registrar log
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
