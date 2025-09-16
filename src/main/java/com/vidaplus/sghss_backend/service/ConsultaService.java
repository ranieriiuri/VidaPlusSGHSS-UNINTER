package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.ConsultaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;

    /**
     * Criar nova consulta
     * Apenas ADMIN ou MEDICO
     */
    public Consulta criarConsulta(Consulta consulta, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar consultas.");
        }

        return consultaRepository.save(consulta);
    }

    /**
     * Listar todas as consultas
     * ADMIN vê todas, MEDICO só as suas, PACIENTE só as suas
     */
    public List<Consulta> listarConsultas(Usuario usuarioLogado) {
        switch (usuarioLogado.getPerfil()) {
            case ADMIN:
                return consultaRepository.findAll();
            case MEDICO:
                return consultaRepository.findByMedicoUsuario(usuarioLogado);
            case PACIENTE:
                return consultaRepository.findByPacienteUsuario(usuarioLogado);
            default:
                throw new AccessDeniedException("Perfil desconhecido.");
        }
    }

    /**
     * Buscar consulta por ID
     * Apenas ADMIN, ou proprietário (medico ou paciente)
     */
    public Consulta buscarPorId(Long id, Usuario usuarioLogado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !consulta.getMedico().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem acessar suas próprias consultas.");
        }

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !consulta.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Pacientes só podem acessar suas próprias consultas.");
        }

        return consulta;
    }

    /**
     * Atualizar consulta
     * Apenas ADMIN ou MEDICO (apenas as suas)
     */
    public Consulta atualizarConsulta(Long id, Consulta consultaAtualizada, Usuario usuarioLogado) {
        Consulta consultaExistente = buscarPorId(id, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !consultaExistente.getMedico().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médicos só podem atualizar suas próprias consultas.");
        }

        consultaExistente.setData(consultaAtualizada.getData());
        consultaExistente.setHora(consultaAtualizada.getHora());
        consultaExistente.setStatus(consultaAtualizada.getStatus());
        consultaExistente.setPaciente(consultaAtualizada.getPaciente());
        consultaExistente.setMedico(consultaAtualizada.getMedico());

        return consultaRepository.save(consultaExistente);
    }

    /**
     * Deletar consulta
     * Apenas ADMIN
     */
    public void deletarConsulta(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar consultas.");
        }

        if (!consultaRepository.existsById(id)) {
            throw new EntityNotFoundException("Consulta não encontrada.");
        }

        consultaRepository.deleteById(id);
    }
}
