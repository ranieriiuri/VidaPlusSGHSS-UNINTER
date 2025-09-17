package com.vidaplus.sghss_backend.controller;

import com.vidaplus.sghss_backend.dto.AgendaMedicaRespostaDTO;
import com.vidaplus.sghss_backend.dto.CriarSlotRequest;
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

    @GetMapping("/medico/{medicoId}")
    public List<AgendaMedicaRespostaDTO> listarSlotsPorMedico(
            @PathVariable Long medicoId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Medico medico = medicoService.buscarEntidadePorId(medicoId, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode listar slots dele mesmo.");
        }
        if (usuarioLogado.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new AccessDeniedException("Paciente não pode acessar agenda de médico.");
        }

        return agendaSlotService.listarSlots(medico);
    }

    // Pacientes tbm podem ver vagas de agenda disponiveis dos medicos
    @GetMapping("/medico/{medicoId}/disponiveis")
    public List<AgendaMedicaRespostaDTO> listarSlotsDisponiveis(
            @PathVariable Long medicoId,
            @RequestParam LocalDate data,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Medico medico = medicoService.buscarEntidadePorId(medicoId, usuarioLogado);

        return agendaSlotService.listarSlotsDisponiveis(medico, data);
    }

    // Recebe id do medico da uri e data e hora do body
    @PostMapping("/medico/{medicoId}/novo")
    public AgendaMedicaRespostaDTO criarSlot(
            @PathVariable Long medicoId,
            @RequestBody CriarSlotRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Medico medico = medicoService.buscarEntidadePorId(medicoId, usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode criar slots para si mesmo.");
        }

        return agendaSlotService.criarSlot(medico, request.getData(), request.getHora(), usuarioLogado);
    }

    @PatchMapping("/{slotId}/disponivel")
    public AgendaMedicaRespostaDTO setDisponivel(
            @PathVariable Long slotId,
            @RequestParam boolean disponivel,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        AgendaMedicaRespostaDTO slot = agendaSlotService.buscarPorId(slotId);
        Medico medico = medicoService.buscarEntidadePorId(slot.getMedicoId(), usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode alterar seus próprios slots.");
        }

        return agendaSlotService.setDisponivel(slotId, disponivel, usuarioLogado);
    }

    // Vincula a vaga disponivel a uma consulta
    @PostMapping("/{slotId}/agendar")
    public AgendaMedicaRespostaDTO vincularConsulta(
            @PathVariable Long slotId,
            @RequestBody Consulta consulta,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        AgendaMedicaRespostaDTO slot = agendaSlotService.buscarPorId(slotId);

        if (!slot.isDisponivel()) {
            throw new IllegalStateException("Slot já está ocupado.");
        }

        // Apenas ADMIN ou o próprio médico podem agendar
        Medico medico = medicoService.buscarEntidadePorId(slot.getMedicoId(), usuarioLogado);

        if (usuarioLogado.getPerfil() == PerfilUsuario.MEDICO &&
                !medico.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new AccessDeniedException("Médico só pode agendar em seus próprios slots.");
        }

        return agendaSlotService.vincularConsulta(slotId, consulta, usuarioLogado);
    }

    // Todos que souberem o id de um slot, podem buscá-lo
    @GetMapping("/{slotId}")
    public AgendaMedicaRespostaDTO buscarSlotPorId(
            @PathVariable Long slotId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        return agendaSlotService.buscarPorId(slotId);
    }

    // Listar todos, para fim de auditoria ou conferência
    @GetMapping("/todos")
    public List<AgendaMedicaRespostaDTO> listarTodosSlots(
            @AuthenticationPrincipal Usuario usuarioLogado) {

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN) {
            throw new AccessDeniedException("Apenas ADMIN pode listar todos os slots.");
        }

        return agendaSlotService.listarTodosSlots();
    }
}
