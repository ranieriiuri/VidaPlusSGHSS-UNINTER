package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.dto.*;
import com.vidaplus.sghss_backend.model.*;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.model.enums.StatusConsulta;
import com.vidaplus.sghss_backend.repository.AgendaMedicaSlotRepository;
import com.vidaplus.sghss_backend.repository.ConsultaRepository;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import com.vidaplus.sghss_backend.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final AuditLogService auditLogService; // ← audit logs
    private final AgendaMedicaSlotRepository agendaMedicaSlotRepository;

    /**
     * Criar nova consulta
     * ADMIN ou MEDICO podem criar
     */
    public Consulta criarConsulta(CriarConsultaRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN &&
                usuarioLogado.getPerfil() != PerfilUsuario.MEDICO) {
            throw new AccessDeniedException("Usuário não autorizado para criar consultas.");
        }

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado."));
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        Consulta consulta = Consulta.builder()
                .data(request.getData())
                .hora(request.getHora())
                .status(StatusConsulta.AGENDADA) // 🔑 garante status inicial
                .paciente(paciente)
                .medico(medico)
                .valor(request.getValor() != null ? request.getValor() : BigDecimal.ZERO) // 🔑 garante não-nulo
                .build();

        // Se o request tiver um slot de agenda
        if (request.getAgendaSlotId() != null) {
            AgendaMedicaSlot slot = agendaMedicaSlotRepository.findById(request.getAgendaSlotId())
                    .orElseThrow(() -> new EntityNotFoundException("Slot de agenda não encontrado."));

            if (!slot.isDisponivel()) {
                throw new IllegalStateException("Este slot já está ocupado.");
            }

            // Faz o vínculo bidirecional
            slot.setConsulta(consulta);
            slot.setDisponivel(false); // marca slot como indisponível
            consulta.setAgendaSlot(slot);
        }

        Consulta salvo = consultaRepository.save(consulta);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_CONSULTA",
                "Consulta",
                salvo.getId(),
                "Paciente: " + paciente.getNome() + ", Médico: " + medico.getNome() +
                        ", Valor: " + salvo.getValor()
        );

        return salvo;
    }


    /**
     * Listar consultas
     */
    public List<ConsultaDTO> listarConsultas(Usuario usuarioLogado) {
        List<Consulta> consultas = switch (usuarioLogado.getPerfil()) {
            case ADMIN -> consultaRepository.findAll();
            case MEDICO -> consultaRepository.findByMedicoUsuario(usuarioLogado);
            case PACIENTE -> consultaRepository.findByPacienteUsuario(usuarioLogado);
            default -> throw new AccessDeniedException("Perfil desconhecido.");
        };

        return consultas.stream()
                .map(ConsultaDTO::from) // método from que converte Consulta -> ConsultaDTO
                .toList();
    }

    // Método interno Relatorios
    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    public ConsultaDTO buscarPorId(Long id, Usuario usuarioLogado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        // Verificação de acesso
        switch (usuarioLogado.getPerfil()) {
            case MEDICO -> {
                if (!consulta.getMedico().getUsuario().getId().equals(usuarioLogado.getId())) {
                    throw new AccessDeniedException("Médicos só podem acessar suas próprias consultas.");
                }
            }
            case PACIENTE -> {
                if (!consulta.getPaciente().getUsuario().getId().equals(usuarioLogado.getId())) {
                    throw new AccessDeniedException("Pacientes só podem acessar suas próprias consultas.");
                }
            }
            case ADMIN -> { /* acesso liberado */ }
            default -> throw new AccessDeniedException("Perfil desconhecido.");
        }

        // Transformar em DTO para retorno
        return ConsultaDTO.from(consulta);
    }

    public Consulta atualizarConsulta(Long id, AtualizarConsultaRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem atualizar consultas.");
        }

        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        // Atualiza apenas campos permitidos
        consultaExistente.setData(request.getData());
        consultaExistente.setHora(request.getHora());
        consultaExistente.setStatus(request.getStatus());

        if (request.getMedicoId() != null) {
            Medico medico = medicoRepository.findById(request.getMedicoId())
                    .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));
            consultaExistente.setMedico(medico);
        }

        if (request.getAgendaSlotId() != null) {
            AgendaMedicaSlot slot = agendaMedicaSlotRepository.findById(request.getAgendaSlotId())
                    .orElseThrow(() -> new EntityNotFoundException("Slot de agenda não encontrado."));
            consultaExistente.setAgendaSlot(slot);
        }

        Consulta salvo = consultaRepository.save(consultaExistente);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "ATUALIZAR_CONSULTA",
                "Consulta",
                salvo.getId(),
                "Paciente: " + salvo.getPaciente().getNome() +
                        ", Médico: " + (salvo.getMedico() != null ? salvo.getMedico().getNome() : "N/A")
        );

        return salvo;
    }

    public BigDecimal obterValorConsulta(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem acessar os valores das consultas.");
        }

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CONSULTAR_VALOR",
                "Consulta",
                consulta.getId(),
                "Paciente: " + consulta.getPaciente().getNome() +
                        ", Médico: " + consulta.getMedico().getNome() +
                        ", Valor: " + consulta.getValor()
        );

        return consulta.getValor();
    }

    public BigDecimal obterTotalConsultasPorMedico(Long medicoId, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem acessar o total por médico.");
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado."));

        BigDecimal total = consultaRepository.findAll()
                .stream()
                .filter(c -> c.getMedico() != null && c.getMedico().getId().equals(medicoId))
                .map(Consulta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Registrar auditoria
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CONSULTAR_TOTAL_VALORES_POR_MEDICO",
                "Consulta",
                null,
                "Total acumulado das consultas para Médico: " + medico.getNome() + " = " + total
        );

        return total;
    }

    public BigDecimal obterTotalConsultas(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem acessar o total das consultas.");
        }

        BigDecimal total = consultaRepository.findAll()
                .stream()
                .map(Consulta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Registrar auditoria
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CONSULTAR_TOTAL_VALORES",
                "Consulta",
                null, // sem ID específico
                "Total acumulado das consultas: " + total
        );

        return total;
    }

    public void deletarConsulta(Long id, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem deletar consultas.");
        }

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada."));

        consultaRepository.delete(consulta);

        // Registrar log
        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "DELETAR_CONSULTA",
                "Consulta",
                consulta.getId(),
                "Paciente: " + consulta.getPaciente().getNome() + ", Médico: " + consulta.getMedico().getNome()
        );
    }
}
