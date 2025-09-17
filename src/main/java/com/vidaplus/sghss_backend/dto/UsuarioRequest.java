package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.enums.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequest {
    private String email;
    private String senha; // plaintext
    private PerfilUsuario perfil;
}
