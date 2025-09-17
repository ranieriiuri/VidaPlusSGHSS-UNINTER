package com.vidaplus.sghss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicoResumoDTO {
    private Long id;
    private String nome;
    private String crm;
    private String especialidade;
}
