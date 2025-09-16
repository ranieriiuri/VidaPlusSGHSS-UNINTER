package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.enums.StatusConsulta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriarConsultaRequest {
    private LocalDate data;
    private LocalTime hora;
    private StatusConsulta status;
    private Long pacienteId;
    private Long medicoId;
}
