package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.AtualizarPacienteRequest;
import com.vidaplus.sghss_backend.model.Notificacao;
import com.vidaplus.sghss_backend.model.Paciente;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import com.vidaplus.sghss_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;
    private final AuditLogService auditLogService; // ← audit logs

    /**
     * Cadastrar paciente
     * Apenas ADMIN ou MEDICO
     */
    public Paciente cadastrarPaciente(Paciente paciente, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para cadastrar pacientes.");
        }

        pacienteRepository.findByCpf(paciente.getCpf())
                .ifPresent(p -> { throw new IllegalArgumentException("CPF já cadastrado."); });

        if (paciente.getUsuario() != null && paciente.getUsuario().getId() != null) {
            Usuario usuario = usuarioRepository.findById(paciente.getUsuario().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário vinculado não encontrado."));
            paciente.setUsuario(usuario);
        }

        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        notificacaoService.enviarNotificacao(
                pacienteSalvo,
                "Paciente cadastrado com sucesso!",
                "OUTROS",
                usuarioLogado
        );

        // Registrar ação no log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CADASTRAR_PACIENTE",
                "Paciente",
                pacienteSalvo.getId(),
                "CPF: " + pacienteSalvo.getCpf()
        );

        return pacienteSalvo;
    }

    public List<Paciente> listarPacientes(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            return pacienteRepository.findByUsuarioId(usuarioLogado.getId())
                    .map(List::of)
                    .orElse(List.of());
        }
        return pacienteRepository.findAll();
    }

    //Método para relatório
    public List<Paciente> listarTodosPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente buscarPorId(Long id, Usuario usuarioLogado) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !paciente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Paciente só pode acessar seus próprios dados.");
        }

        return paciente;
    }

    @Transactional
    public Paciente atualizarPaciente(Long id, AtualizarPacienteRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para atualizar pacientes.");
        }

        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        // 🔑 vincular usuário, se vier no request e ainda não estiver vinculado
        if (request.usuarioId() != null && pacienteExistente.getUsuario() == null) {
            Usuario usuario = usuarioRepository.findById(request.usuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

            if (usuario.getPerfil() != PerfilUsuario.PACIENTE) {
                throw new IllegalArgumentException("Somente usuários com perfil PACIENTE podem ser vinculados.");
            }

            pacienteExistente.setUsuario(usuario);
        }

        pacienteExistente.setNome(request.nome());
        pacienteExistente.setDataNascimento(request.dataNascimento());
        pacienteExistente.setEndereco(request.endereco());
        pacienteExistente.setTelefone(request.telefone());
        pacienteExistente.setTeleconsultaInfo(request.teleconsultaInfo());

        Paciente pacienteAtualizado = pacienteRepository.save(pacienteExistente);

        notificacaoService.enviarNotificacao(
                pacienteAtualizado,
                "Seus dados foram atualizados.",
                "OUTROS",
                usuarioLogado
        );

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_PACIENTE",
                "Paciente",
                pacienteAtualizado.getId(),
                "Nome: " + pacienteAtualizado.getNome() + ", Telefone: " + pacienteAtualizado.getTelefone()
        );

        return pacienteAtualizado;
    }


    public void deletarPaciente(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar pacientes.");
        }

        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        pacienteRepository.delete(pacienteExistente);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "DELETAR_PACIENTE",
                "Paciente",
                pacienteExistente.getId(),
                "Nome: " + pacienteExistente.getNome() + ", Telefone: " + pacienteExistente.getTelefone()
        );
    }


    public List<Notificacao> listarNotificacoes(Long pacienteId, Usuario usuarioLogado) {
        Paciente paciente = buscarPorId(pacienteId, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !paciente.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Paciente só pode acessar suas próprias notificações.");
        }

        return notificacaoService.listarNotificacoesPaciente(paciente);
    }

    public void marcarNotificacaoComoLida(Long notificacaoId, Usuario usuarioLogado) {
        Notificacao notificacao = notificacaoService.buscarPorId(notificacaoId);

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE &&
                !notificacao.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Paciente só pode marcar suas próprias notificações.");
        }

        notificacaoService.marcarComoLida(notificacaoId, usuarioLogado);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "MARCAR_NOTIFICACAO_LIDA",
                "Notificacao",
                notificacaoId,
                null
        );
    }
}
