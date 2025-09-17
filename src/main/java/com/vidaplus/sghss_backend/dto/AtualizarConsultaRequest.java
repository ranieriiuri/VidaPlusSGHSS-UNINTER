package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.enums.StatusConsulta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarConsultaRequest {
    private LocalDate data;
    private LocalTime hora;
    private StatusConsulta status;
    private Long medicoId;       // opcional, para atualizar médico
    private Long agendaSlotId;   // opcional, para atualizar slot
}
