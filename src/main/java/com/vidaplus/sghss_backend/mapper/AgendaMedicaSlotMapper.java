package com.vidaplus.sghss_backend.mapper;

import com.vidaplus.sghss_backend.dto.AgendaMedicaRespostaDTO;
import com.vidaplus.sghss_backend.model.AgendaMedicaSlot;

public class AgendaMedicaSlotMapper {

    public static AgendaMedicaRespostaDTO toDTO(AgendaMedicaSlot slot) {
        if (slot == null) return null;

        return AgendaMedicaRespostaDTO.builder()
                .id(slot.getId())
                .data(slot.getData())
                .hora(slot.getHora())
                .disponivel(slot.isDisponivel())
                .medicoId(slot.getMedico() != null ? slot.getMedico().getId() : null)
                .medicoNome(slot.getMedico() != null ? slot.getMedico().getNome() : null)
                .consultaId(slot.getConsulta() != null ? slot.getConsulta().getId() : null)
                .pacienteNome(slot.getConsulta() != null ? slot.getConsulta().getPaciente().getNome() : null)
                .build();
    }
}
