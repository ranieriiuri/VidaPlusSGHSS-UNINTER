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

import com.vidaplus.sghss_backend.dto.AgendaMedicaRespostaDTO;
import com.vidaplus.sghss_backend.mapper.AgendaMedicaSlotMapper;

@Service
@RequiredArgsConstructor
public class AgendaMedicaSlotService {

    private final AgendaMedicaSlotRepository agendaSlotRepository;
    private final MedicoRepository medicoRepository;
    private final AuditLogService auditLogService;

    public List<AgendaMedicaRespostaDTO> listarSlots(Medico medico) {
        return agendaSlotRepository.findByMedico(medico)
                .stream()
                .map(AgendaMedicaSlotMapper::toDTO)
                .toList();
    }

    public List<AgendaMedicaRespostaDTO> listarSlotsDisponiveis(Medico medico, LocalDate data) {
        return agendaSlotRepository.findByMedicoAndDataAndDisponivelTrue(medico, data)
                .stream()
                .map(AgendaMedicaSlotMapper::toDTO)
                .toList();
    }

    public List<AgendaMedicaRespostaDTO> listarTodosSlots() {
        return agendaSlotRepository.findAll()
                .stream()
                .map(AgendaMedicaSlotMapper::toDTO)
                .toList();
    }

    public AgendaMedicaRespostaDTO criarSlot(Medico medico, LocalDate data, LocalTime hora, Usuario usuarioLogado) {
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

        return AgendaMedicaSlotMapper.toDTO(salvo);
    }

    public AgendaMedicaRespostaDTO setDisponivel(Long slotId, boolean disponivel, Usuario usuarioLogado) {
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

        return AgendaMedicaSlotMapper.toDTO(salvo);
    }

    public AgendaMedicaRespostaDTO buscarPorId(Long slotId) {
        AgendaMedicaSlot slot = agendaSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot não encontrado."));
        return AgendaMedicaSlotMapper.toDTO(slot);
    }
}
