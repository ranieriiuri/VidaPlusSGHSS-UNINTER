package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cadastrar paciente no sistema
     * Somente usuários autorizados devem chamar esse método (ADMIN / MEDICO)
     */
    public Paciente cadastrarPaciente(Paciente paciente) {
        // Verificar CPF duplicado
        pacienteRepository.findByCpf(paciente.getCpf())
                .ifPresent(p -> { throw new IllegalArgumentException("CPF já cadastrado."); });

        // Futuro: vincular o usuário ao paciente
        // paciente.setUsuario(...);

        return pacienteRepository.save(paciente);
    }

    /**
     * Listar todos os pacientes
     */
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    /**
     * Buscar paciente por ID
     */
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));
    }

    /**
     * Atualizar dados de um paciente
     */
    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
        Paciente pacienteExistente = buscarPorId(id);

        pacienteExistente.setNome(pacienteAtualizado.getNome());
        pacienteExistente.setCpf(pacienteAtualizado.getCpf());
        pacienteExistente.setDataNascimento(pacienteAtualizado.getDataNascimento());
        pacienteExistente.setEndereco(pacienteAtualizado.getEndereco());
        pacienteExistente.setTelefone(pacienteAtualizado.getTelefone());

        return pacienteRepository.save(pacienteExistente);
    }

    /**
     * Deletar paciente
     */
    public void deletarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }
        pacienteRepository.deleteById(id);
    }
}