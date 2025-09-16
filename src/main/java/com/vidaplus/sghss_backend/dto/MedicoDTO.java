package com.vidaplus.sghss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoDTO {
    private Long id;
    private String nome;
    private String crm;
    private String especialidade;
    private Long usuarioId;
}
