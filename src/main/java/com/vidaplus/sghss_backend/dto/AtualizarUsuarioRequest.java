package com.vidaplus.sghss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtualizarUsuarioRequest {
    private Long id;
    private String email;
    private String perfil; // MEDICO, PACIENTE, ADMIN
}