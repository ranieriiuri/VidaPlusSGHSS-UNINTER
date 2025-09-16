package com.vidaplus.sghss_backend.service;

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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;

    /**
     * Criar novo prontuário
     * Apenas ADMIN ou MEDICO podem criar
     * Permite múltiplos prontuários por paciente, mas impede duplicidade de 'registros'
     */
    public Prontuario criarProntuario(Prontuario prontuario, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar prontuários.");
        }

        Long pacienteId = prontuario.getPaciente().getId();
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));

        // Evita duplicidade de 'registros' entre múltiplos prontuários
        boolean duplicado = paciente.getProntuarios().stream()
                .anyMatch(p -> p.getRegistros() != null && p.getRegistros().equals(prontuario.getRegistros()));

        if (duplicado) {
            throw new IllegalArgumentException("Já existe um prontuário para este paciente com o mesmo registro.");
        }

        prontuario.setPaciente(paciente);
        paciente.getProntuarios().add(prontuario);

        return prontuarioRepository.save(prontuario);
    }

    /**
     * Listar todos os prontuários
     * ADMIN e MEDICO veem todos
     * PACIENTE vê apenas seus próprios prontuários
     */
    public List<Prontuario> listarProntuarios(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            Paciente paciente = pacienteRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));
            return new ArrayList<>(paciente.getProntuarios());
        }
        return prontuarioRepository.findAll();
    }

    /**
     * Buscar prontuário por ID
     * PACIENTE só pode acessar seu próprio
     */
    public Prontuario buscarPorId(Long id, Usuario usuarioLogado) {
        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !prontuario.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Pacientes só podem acessar seus próprios prontuários.");
        }

        return prontuario;
    }

    /**
     * Atualizar prontuário
     * Apenas ADMIN ou MEDICO
     */
    public Prontuario atualizarProntuario(Long id, Prontuario prontuarioAtualizado, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para atualizar prontuários.");
        }

        Prontuario prontuarioExistente = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        prontuarioExistente.setRegistros(prontuarioAtualizado.getRegistros());
        prontuarioExistente.setPrescricoes(prontuarioAtualizado.getPrescricoes());

        return prontuarioRepository.save(prontuarioExistente);
    }

    /**
     * Deletar prontuário
     * Apenas ADMIN
     */
    public void deletarProntuario(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar prontuários.");
        }

        Prontuario prontuario = prontuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prontuário não encontrado."));

        // Remove da lista do paciente antes de deletar
        prontuario.getPaciente().getProntuarios().remove(prontuario);

        prontuarioRepository.delete(prontuario);
    }
}
