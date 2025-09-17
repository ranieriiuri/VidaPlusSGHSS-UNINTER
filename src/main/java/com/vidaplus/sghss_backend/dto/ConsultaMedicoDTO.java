package com.vidaplus.sghss_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsultaMedicoDTO {
    private Long id;
    private String data;
    private String hora;
    private String status;
    private PacienteDTO paciente;
}