package com.vidaplus.sghss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO simplificado para Usuário no relatório
@Data
@AllArgsConstructor
public class AdminRespostaDTO {
    private Long id;
    private String email;
    private String perfil;
}
