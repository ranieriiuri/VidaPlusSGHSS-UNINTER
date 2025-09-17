package com.vidaplus.sghss_backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AgendaMedicaRespostaDTO {
    private Long id;
    private LocalDate data;
    private LocalTime hora;
    private boolean disponivel;

    private Long medicoId;
    private String medicoNome;

    private Long consultaId;
    private String pacienteNome;
}
