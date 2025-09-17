package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.AgendaMedicaSlot;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AgendaMedicaSlotDTO {
    private Long id;
    private LocalDate data;
    private LocalTime hora;
    private boolean disponivel;

    public static AgendaMedicaSlotDTO from(AgendaMedicaSlot slot) {
        return AgendaMedicaSlotDTO.builder()
                .id(slot.getId())
                .data(slot.getData())
                .hora(slot.getHora())
                .disponivel(slot.isDisponivel())
                .build();
    }
}