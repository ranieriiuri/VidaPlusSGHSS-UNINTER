package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.enums.StatusConsulta;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VincularConsultaRequest {
    Long pacienteId;
    StatusConsulta status;
}