package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;

    /**
     * Criar médico
     * Apenas ADMIN
     */
    public Medico criarMedico(Medico medico, Usuario usuarioLogado) {
        if (!"ADMIN".equals(usuarioLogado.getPerfil())) {
            throw new AccessDeniedException("Apenas administradores podem criar médicos.");
        }

        return medicoRepository.save(medico);
    }

    /**
     * Listar médicos
     * ADMIN vê todos
     * MEDICO vê todos
     */
    public List<Medico> listarMedicos(Usuario usuarioLogado) {
        if ("PACIENTE".equals(usuarioLogado.getPerfil())) {
            throw new AccessDeniedException("Pacientes não podem listar médicos.");
        }
        return medicoRepository.findAll();
    }

    /**
     * Buscar médico por ID
     */
    public Medico buscarPorId(Long id, Usuario usuarioLogado) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));

        if ("MEDICO".equals(usuarioLogado.getPerfil()) &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem acessar seus próprios dados.");
        }

        if ("PACIENTE".equals(usuarioLogado.getPerfil())) {
            throw new AccessDeniedException("Pacientes não podem acessar médicos.");
        }

        return medico;
    }

    /**
     * Atualizar médico
     */
    public Medico atualizarMedico(Long id, Medico medicoAtualizado, Usuario usuarioLogado) {
        Medico medicoExistente = buscarPorId(id, usuarioLogado);

        if ("MEDICO".equals(usuarioLogado.getPerfil()) &&
                !medicoExistente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem atualizar seus próprios dados.");
        }

        medicoExistente.setNome(medicoAtualizado.getNome());
        medicoExistente.setEspecialidade(medicoAtualizado.getEspecialidade());
        medicoExistente.setCrm(medicoAtualizado.getCrm());

        return medicoRepository.save(medicoExistente);
    }

    /**
     * Deletar médico
     * Apenas ADMIN
     */
    public void deletarMedico(Long id, Usuario usuarioLogado) {
        if (!"ADMIN".equals(usuarioLogado.getPerfil())) {
            throw new AccessDeniedException("Apenas administradores podem deletar médicos.");
        }

        if (!medicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }

        medicoRepository.deleteById(id);
    }
}
