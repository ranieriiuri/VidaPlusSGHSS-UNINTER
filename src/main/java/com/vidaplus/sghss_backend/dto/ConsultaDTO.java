package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Consulta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultaDTO {
    private Long id;
    private String data;
    private String hora;
    private String status;

    private PacienteDTO paciente;
    private MedicoDTO medico;
    private AgendaMedicaSlotDTO agendaSlot; // incluir detalhes da agenda medica

    public static ConsultaDTO from(Consulta consulta) {
        return ConsultaDTO.builder()
                .id(consulta.getId())
                .data(consulta.getData().toString())
                .hora(consulta.getHora().toString())
                .status(consulta.getStatus().name())
                .paciente(PacienteDTO.from(consulta.getPaciente()))
                .medico(MedicoDTO.from(consulta.getMedico()))
                .agendaSlot(consulta.getAgendaSlot() != null ?
                        AgendaMedicaSlotDTO.from(consulta.getAgendaSlot()) : null)
                .build();
    }
}
