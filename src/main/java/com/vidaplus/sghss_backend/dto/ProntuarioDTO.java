package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Prontuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProntuarioDTO {

    private Long id;
    private String registros;
    private String prescricoes;
    private Long pacienteId;
    private String pacienteNome;
    private Long medicoId;
    private String medicoNome;

    public static ProntuarioDTO from(Prontuario p) {
        return ProntuarioDTO.builder()
                .id(p.getId())
                .registros(p.getRegistros())
                .prescricoes(p.getPrescricoes())
                .pacienteId(p.getPaciente().getId())
                .pacienteNome(p.getPaciente().getNome())
                .medicoId(p.getMedico() != null ? p.getMedico().getId() : null)
                .medicoNome(p.getMedico() != null ? p.getMedico().getNome() : null)
                .build();
    }
}
