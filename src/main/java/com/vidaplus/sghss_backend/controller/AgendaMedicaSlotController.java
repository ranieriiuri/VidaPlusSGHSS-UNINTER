package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.MedicoDTO;
import com.vidaplus.sghss_backend.model.AgendaMedicaSlot;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
import com.vidaplus.sghss_backend.model.Usuario;
import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import com.vidaplus.sghss_backend.service.AgendaMedicaSlotService;
import com.vidaplus.sghss_backend.service.MedicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/agenda-medica")
@RequiredArgsConstructor
public class AgendaMedicaSlotController {

    private final AgendaMedicaSlotService agendaSlotService;
    private final MedicoService medicoService;

    /**
     * Listar todos os slots de um médico
     */
    @GetMapping("/medico/{medicoId}")
    public List<AgendaMedicaSlot> listarSlots(
            @PathVariable Long medicoId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Medico medico = medicoService.buscarEntidadePorId(medicoId, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Paciente não pode acessar agenda de médico.");
        }

        return agendaSlotService.listarSlots(medico);
    }

    /**
     * Listar slots disponíveis de um médico em uma data
     */
    @GetMapping("/medico/{medicoId}/disponiveis")
    public List<AgendaMedicaSlot> listarSlotsDisponiveis(
            @PathVariable Long medicoId,
            @RequestParam LocalDate data,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Medico medico = medicoService.buscarEntidadePorId(medicoId, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Paciente não pode acessar agenda de médico.");
        }

        return agendaSlotService.listarSlotsDisponiveis(medico, data);
    }

    /**
     * Criar um novo slot
     */
    @PostMapping("/medico/{medicoId}/novo")
    public AgendaMedicaSlot criarSlot(
            @PathVariable Long medicoId,
            @RequestParam LocalDate data,
            @RequestParam LocalTime hora,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Medico medico = medicoService.buscarEntidadePorId(medicoId, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode criar slots para si mesmo.");
        }

        return agendaSlotService.criarSlot(medico, data, hora, usuarioLogado);
    }

    /**
     * Bloquear ou liberar slot
     */
    @PatchMapping("/{slotId}/disponivel")
    public AgendaMedicaSlot setDisponivel(
            @PathVariable Long slotId,
            @RequestParam boolean disponivel,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        AgendaMedicaSlot slot = agendaSlotService.buscarPorId(slotId);
        Medico medico = slot.getMedico();

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode alterar seus próprios slots.");
        }

        return agendaSlotService.setDisponivel(slotId, disponivel, usuarioLogado);
    }

    /**
     * Vincular slot a uma consulta
     */
    @PostMapping("/{slotId}/agendar")
    public AgendaMedicaSlot agendarConsulta(
            @PathVariable Long slotId,
            @RequestBody Consulta consulta,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        AgendaMedicaSlot slot = agendaSlotService.buscarPorId(slotId);

        if (!slot.isDisponivel()) {
            throw new IllegalStateException("Slot já está ocupado.");
        }

        // Apenas ADMIN ou o próprio médico podem agendar
        Medico medico = slot.getMedico();
        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode agendar em seus próprios slots.");
        }

        return agendaSlotService.vincularConsulta(slotId, consulta, usuarioLogado);
    }
}
