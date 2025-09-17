package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Paciente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PacienteDTO {
    private Long id;
    private String nome;
    private String cpf;

    public static PacienteDTO from(Paciente paciente) {
        return PacienteDTO.builder()
                .id(paciente.getId())
                .nome(paciente.getNome())
                .cpf(paciente.getCpf())
                .build();
    }
}