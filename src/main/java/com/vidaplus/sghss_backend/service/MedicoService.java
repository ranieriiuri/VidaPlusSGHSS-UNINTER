package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.MedicoDTO;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Paciente;
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

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;

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

        // Buscar o usuário gerenciado pelo JPA
        Usuario usuario = usuarioRepository.findById(medico.getUsuario().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário associado não encontrado."));

        // Verificar perfil do usuário
        if (usuario.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new IllegalArgumentException("Não é possível associar um paciente como médico.");
        }

        // Verificar se já existe outro médico vinculado a esse usuário
        if (medicoRepository.existsByUsuario(usuario)) {
            throw new IllegalArgumentException("Usuário já está associado a outro médico.");
        }

        // Criar uma nova instância para salvar
        Medico medicoParaSalvar = Medico.builder()
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .especialidade(medico.getEspecialidade())
                .usuario(usuario) // somente o usuário gerenciado
                .build();

        return medicoRepository.save(medicoParaSalvar);
    }


    /**
     * Listar médicos
     * ADMIN e MEDICO podem listar
     */
    public List<MedicoDTO> listarMedicos(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Pacientes não podem listar médicos.");
        }

        return medicoRepository.findAll()
                .stream()
                .map(medico -> MedicoDTO.builder()
                        .id(medico.getId())
                        .nome(medico.getNome())
                        .crm(medico.getCrm())
                        .especialidade(medico.getEspecialidade())
                        .usuarioId(medico.getUsuario() != null ? medico.getUsuario().getId() : null)
                        .build())
                .toList();
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

    /**
     * Buscar médico por ID
     */
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


    /**
     * Atualizar médico
     */
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

        // NÃO permite alterar o usuário associado
        return medicoRepository.save(medicoExistente);
    }

    /**
     * Deletar médico
     * Apenas ADMIN
     */
    @Transactional
    public void deletarMedico(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar médicos.");
        }

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        // Quebrar vínculo dos dois lados
        Usuario usuario = medico.getUsuario();
        if (usuario != null) {
            usuario.setMedico(null);
            medico.setUsuario(null);
        }

        // Se houver consultas associadas, limpamos para não sobrar órfãos
        if (medico.getConsultas() != null) {
            medico.getConsultas().clear();
        }

        medicoRepository.delete(medico);
    }

}
