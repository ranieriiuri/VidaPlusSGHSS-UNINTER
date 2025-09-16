package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.AtualizarPacienteRequest;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cadastrar paciente
     * Apenas ADMIN ou MEDICO
     */
    public Paciente cadastrarPaciente(Paciente paciente, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para cadastrar pacientes.");
        }

        // Verificar CPF duplicado
        pacienteRepository.findByCpf(paciente.getCpf())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("CPF já cadastrado.");
                });

        // Vincular usuário ao paciente, se informado
        if (paciente.getUsuario() != null && paciente.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(paciente.getUsuario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário vinculado não encontrado."));
            paciente.setUsuario(usuario);
        }

        return pacienteRepository.save(paciente);
    }

    /**
     * Listar todos os pacientes
     * ADMIN vê todos, MEDICO vê todos, PACIENTE só vê ele mesmo
     */
    public List<Paciente> listarPacientes(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            return pacienteRepository.findByUsuario(usuarioLogado)
                    .map(List::of)
                    .orElse(List.of());
        }

        // ADMIN ou MEDICO
        return pacienteRepository.findAll();
    }

    /**
     * Buscar paciente por ID
     */
    public Paciente buscarPorId(Long id, Usuario usuarioLogado) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !paciente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Paciente só pode acessar seus próprios dados.");
        }

        return paciente;
    }

    /**
     * Atualizar paciente
     * Apenas ADMIN ou MEDICO
     */
    public Paciente atualizarPaciente(Long id, AtualizarPacienteRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para atualizar pacientes.");
        }

        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        pacienteExistente.setNome(request.nome());
        pacienteExistente.setDataNascimento(request.dataNascimento());
        pacienteExistente.setEndereco(request.endereco());
        pacienteExistente.setTelefone(request.telefone());

        // NÃO alteramos pacienteExistente.setUsuario(...)

        return pacienteRepository.save(pacienteExistente);
    }


    /**
     * Deletar paciente
     * Apenas ADMIN
     */
    public void deletarPaciente(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar pacientes.");
        }

        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }

        pacienteRepository.deleteById(id);
    }
}
