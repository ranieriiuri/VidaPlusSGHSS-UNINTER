package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioCompletoDTO {

    private List<Paciente> pacientes;
    private List<Medico> medicos;
    private List<Consulta> consultas;
    private List<Prontuario> prontuarios;
    private List<AgendaMedicaRespostaDTO> slots;

}
