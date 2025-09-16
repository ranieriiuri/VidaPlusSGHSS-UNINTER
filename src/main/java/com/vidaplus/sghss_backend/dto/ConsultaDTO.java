package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Consulta;
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
public class ConsultaDTO {

    private Long id;
    private LocalDate data;
    private LocalTime hora;
    private StatusConsulta status;
    private Long pacienteId;
    private Long medicoId;

    // Converte entidade para DTO
    public static ConsultaDTO from(Consulta consulta) {
        return ConsultaDTO.builder()
                .id(consulta.getId())
                .data(consulta.getData())
                .hora(consulta.getHora())
                .status(consulta.getStatus())
                .pacienteId(consulta.getPaciente().getId())
                .medicoId(consulta.getMedico().getId())
                .build();
    }
}
