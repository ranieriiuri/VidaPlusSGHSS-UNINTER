package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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
        if (!usuarioLogado.getPerfil().equals("ADMIN") && !usuarioLogado.getPerfil().equals("MEDICO")) {
            throw new SecurityException("Usuário não autorizado para cadastrar pacientes.");
        }

        // Verificar CPF duplicado
        pacienteRepository.findByCpf(paciente.getCpf())
                .ifPresent(p -> { throw new IllegalArgumentException("CPF já cadastrado."); });

        // Futuro: vincular o usuário ao paciente
        // paciente.setUsuario(...);

        return pacienteRepository.save(paciente);
    }

    /**
     * Listar todos os pacientes
     * ADMIN vê todos, MEDICO vê todos, PACIENTE só vê ele mesmo
     */
    public List<Paciente> listarPacientes(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil().equals("PACIENTE")) {
            return pacienteRepository.findByUsuario(usuarioLogado)
                    .map(p -> List.<Paciente>of(p)) // força List<Paciente>
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

        if (usuarioLogado.getPerfil().equals("PACIENTE") && !paciente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new SecurityException("Paciente só pode acessar seus próprios dados.");
        }

        return paciente;
    }

    /**
     * Atualizar paciente
     * ADMIN ou MEDICO
     */
    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado, Usuario usuarioLogado) {
        if (!usuarioLogado.getPerfil().equals("ADMIN") && !usuarioLogado.getPerfil().equals("MEDICO")) {
            throw new SecurityException("Usuário não autorizado para atualizar pacientes.");
        }

        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        pacienteExistente.setNome(pacienteAtualizado.getNome());
        pacienteExistente.setCpf(pacienteAtualizado.getCpf());
        pacienteExistente.setDataNascimento(pacienteAtualizado.getDataNascimento());
        pacienteExistente.setEndereco(pacienteAtualizado.getEndereco());
        pacienteExistente.setTelefone(pacienteAtualizado.getTelefone());

        return pacienteRepository.save(pacienteExistente);
    }

    /**
     * Deletar paciente
     * Apenas ADMIN
     */
    public void deletarPaciente(Long id, Usuario usuarioLogado) {
        if (!usuarioLogado.getPerfil().equals("ADMIN")) {
            throw new SecurityException("Apenas administradores podem deletar pacientes.");
        }

        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }

        pacienteRepository.deleteById(id);
    }

}
