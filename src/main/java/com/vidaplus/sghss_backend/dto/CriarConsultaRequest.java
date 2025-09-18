package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.enums.StatusConsulta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CriarConsultaRequest {
    private Long pacienteId;
    private Long medicoId;
    private Long agendaSlotId; // opcional
    private LocalDate data;    // opcional se usar slot
    private LocalTime hora;    // opcional se usar slot
    private StatusConsulta status;
    private BigDecimal valor;
}
