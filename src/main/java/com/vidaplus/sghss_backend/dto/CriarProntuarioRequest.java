package com.vidaplus.sghss_backend.dto;

public record CriarProntuarioRequest(
        Long pacienteId,
        String registros,
        String prescricoes
) {}
