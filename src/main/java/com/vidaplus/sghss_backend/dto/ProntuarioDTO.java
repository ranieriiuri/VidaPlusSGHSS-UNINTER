package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Prontuario;

public record ProntuarioDTO(
        Long id,
        String registros,
        String prescricoes,
        Long pacienteId,
        String pacienteNome
) {
    public static ProntuarioDTO from(Prontuario prontuario) {
        return new ProntuarioDTO(
                prontuario.getId(),
                prontuario.getRegistros(),
                prontuario.getPrescricoes(),
                prontuario.getPaciente().getId(),
                prontuario.getPaciente().getNome()
        );
    }
}
