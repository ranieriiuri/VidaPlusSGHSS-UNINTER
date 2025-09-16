package com.vidaplus.sghss_backend.dto;

import java.time.LocalDate;

public record AtualizarPacienteRequest(
        String nome,
        LocalDate dataNascimento,
        String endereco,
        String telefone,
        String teleconsultaInfo
) {}
