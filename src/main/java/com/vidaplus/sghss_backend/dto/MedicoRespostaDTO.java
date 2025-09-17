package com.vidaplus.sghss_backend.dto;

// MedicoDTO.java
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MedicoRespostaDTO {
    private Long id;
    private String nome;
    private String crm;
    private String especialidade;
    private Long usuarioId;
    private List<ConsultaDTO> consultas;
    private List<AgendaMedicaSlotDTO> agendaSlots;
}