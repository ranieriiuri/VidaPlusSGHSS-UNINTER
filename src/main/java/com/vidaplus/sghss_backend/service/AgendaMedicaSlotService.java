package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.AgendaMedicaSlot;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.repository.AgendaMedicaSlotRepository;
import com.vidaplus.sghss_backend.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaMedicaSlotService {

    private final AgendaMedicaSlotRepository agendaSlotRepository;
    private final MedicoRepository medicoRepository;
    private final AuditLogService auditLogService; // ← adicionado para logs

    // Listar todos os slots de um médico
    public List<AgendaMedicaSlot> listarSlots(Medico medico) {
        return agendaSlotRepository.findByMedico(medico);
    }

    // Listar slots disponíveis de um médico
    public List<AgendaMedicaSlot> listarSlotsDisponiveis(Medico medico, LocalDate data) {
        return agendaSlotRepository.findByMedicoAndDataAndDisponivelTrue(medico, data);
    }

    public List<AgendaMedicaSlot> listarTodosSlots() {
        return agendaSlotRepository.findAll();
    }

    // Criar um novo slot
    public AgendaMedicaSlot criarSlot(Medico medico, LocalDate data, LocalTime hora, Usuario usuarioLogado) {
        if (agendaSlotRepository.existsByMedicoAndDataAndHora(medico, data, hora)) {
            throw new IllegalArgumentException("Slot já existe para essa data e hora.");
        }

        AgendaMedicaSlot slot = AgendaMedicaSlot.builder()
                .medico(medico)
                .data(data)
                .hora(hora)
                .disponivel(true)
                .build();

        AgendaMedicaSlot salvo = agendaSlotRepository.save(slot);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "CRIAR_SLOT",
                "AgendaMedicaSlot",
                salvo.getId(),
                "Médico: " + medico.getNome() + ", Data: " + data + ", Hora: " + hora
        );

        return salvo;
    }

    // Bloquear/Desbloquear slot
    public AgendaMedicaSlot setDisponivel(Long slotId, boolean disponivel, Usuario usuarioLogado) {
        AgendaMedicaSlot slot = agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
        slot.setDisponivel(disponivel);
        AgendaMedicaSlot salvo = agendaSlotRepository.save(slot);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                disponivel ? "DESBLOQUEAR_SLOT" : "BLOQUEAR_SLOT",
                "AgendaMedicaSlot",
                slotId,
                "Médico: " + slot.getMedico().getNome() + ", Data: " + slot.getData() + ", Hora: " + slot.getHora()
        );

        return salvo;
    }

    // Vincular slot a uma consulta
    public AgendaMedicaSlot vincularConsulta(Long slotId, Consulta consulta, Usuario usuarioLogado) {
        AgendaMedicaSlot slot = agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
        if (!slot.isDisponivel()) {
            throw new IllegalStateException("Slot já está ocupado.");
        }
        slot.setConsulta(consulta);
        slot.setDisponivel(false);
        AgendaMedicaSlot salvo = agendaSlotRepository.save(slot);

        auditLogService.registrarAcao(
                usuarioLogado.getId(),
                usuarioLogado.getEmail(),
                usuarioLogado.getPerfil().name(),
                "VINCULAR_CONSULTA_SLOT",
                "AgendaMedicaSlot",
                slotId,
                "Consulta ID: " + consulta.getId() + ", Paciente: " + consulta.getPaciente().getNome() +
                        ", Médico: " + consulta.getMedico().getNome() +
                        ", Data: " + slot.getData() + ", Hora: " + slot.getHora()
        );

        return salvo;
    }

    public AgendaMedicaSlot buscarPorId(Long slotId) {
        return agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
    }
}
