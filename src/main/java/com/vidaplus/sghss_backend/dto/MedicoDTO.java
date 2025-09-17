package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Medico;
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

    public static MedicoDTO from(Medico medico) {
        return MedicoDTO.builder()
                .id(medico.getId())
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .especialidade(medico.getEspecialidade())
                .build();
    }
}
