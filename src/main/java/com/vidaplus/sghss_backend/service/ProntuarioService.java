package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
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

    /**
     * Criar novo prontuário
     * Apenas ADMIN ou MEDICO podem criar
     * Verifica se já existe prontuário para o paciente
     */
    public Prontuario criarProntuario(Prontuario prontuario, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar prontuários.");
        }

        prontuarioRepository.findByPaciente(prontuario.getPaciente())
                .ifPresent(p -> { throw new IllegalArgumentException("Prontuário já existe para este paciente."); });

        return prontuarioRepository.save(prontuario);
    }

    /**
     * Listar todos os prontuários
     * ADMIN e MEDICO veem todos
     * PACIENTE vê apenas seu próprio prontuário
     */
    public List<Prontuario> listarProntuarios(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            return prontuarioRepository.findByPacienteUsuario(usuarioLogado)
                    .map(List::of)
                    .orElse(List.of());
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
            throw new AccessDeniedException("Pacientes só podem acessar seu próprio prontuário.");
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

        if (!prontuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Prontuário não encontrado.");
        }

        prontuarioRepository.deleteById(id);
    }
}
