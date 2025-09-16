package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.AgendaMedicaSlot;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
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
    public AgendaMedicaSlot criarSlot(Medico medico, LocalDate data, LocalTime hora) {
        if (agendaSlotRepository.existsByMedicoAndDataAndHora(medico, data, hora)) {
            throw new IllegalArgumentException("Slot já existe para essa data e hora.");
        }

        AgendaMedicaSlot slot = AgendaMedicaSlot.builder()
                .medico(medico)
                .data(data)
                .hora(hora)
                .disponivel(true)
                .build();

        return agendaSlotRepository.save(slot);
    }

    // Bloquear/Desbloquear slot
    public AgendaMedicaSlot setDisponivel(Long slotId, boolean disponivel) {
        AgendaMedicaSlot slot = agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
        slot.setDisponivel(disponivel);
        return agendaSlotRepository.save(slot);
    }

    // Vincular slot a uma consulta
    public AgendaMedicaSlot vincularConsulta(Long slotId, Consulta consulta) {
        AgendaMedicaSlot slot = agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
        if (!slot.isDisponivel()) {
            throw new IllegalStateException("Slot já está ocupado.");
        }
        slot.setConsulta(consulta);
        slot.setDisponivel(false);
        return agendaSlotRepository.save(slot);
    }

    public AgendaMedicaSlot buscarPorId(Long slotId) {
        return agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
    }
}
