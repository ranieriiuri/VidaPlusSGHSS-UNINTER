package com.vidaplus.sghss_backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CriarSlotRequest {
    private LocalDate data;
    private LocalTime hora;
}
