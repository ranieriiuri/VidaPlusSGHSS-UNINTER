package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Prontuario;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        if (!"ADMIN".equals(usuarioLogado.getPerfil()) && !"MEDICO".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Usuário não autorizado para criar prontuários.");
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
        if ("PACIENTE".equals(usuarioLogado.getPerfil())) {
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
                .orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado."));

        if ("PACIENTE".equals(usuarioLogado.getPerfil())
                && !prontuario.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new SecurityException("Pacientes só podem acessar seu próprio prontuário.");
        }

        return prontuario;
    }

    /**
     * Atualizar prontuário
     * Apenas ADMIN ou MEDICO
     */
    public Prontuario atualizarProntuario(Long id, Prontuario prontuarioAtualizado, Usuario usuarioLogado) {
        if (!"ADMIN".equals(usuarioLogado.getPerfil()) && !"MEDICO".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Usuário não autorizado para atualizar prontuários.");
        }

        Prontuario prontuarioExistente = prontuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado."));

        prontuarioExistente.setRegistros(prontuarioAtualizado.getRegistros());
        prontuarioExistente.setPrescricoes(prontuarioAtualizado.getPrescricoes());

        return prontuarioRepository.save(prontuarioExistente);
    }

    /**
     * Deletar prontuário
     * Apenas ADMIN
     */
    public void deletarProntuario(Long id, Usuario usuarioLogado) {
        if (!"ADMIN".equals(usuarioLogado.getPerfil())) {
            throw new SecurityException("Apenas administradores podem deletar prontuários.");
        }

        if (!prontuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Prontuário não encontrado.");
        }

        prontuarioRepository.deleteById(id);
    }
}
