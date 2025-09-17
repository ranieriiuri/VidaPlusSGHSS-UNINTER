package com.vidaplus.sghss_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vidaplus.sghss_backend.model.Consulta;
import com.vidaplus.sghss_backend.model.Medico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agenda_medica_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMedicaSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;
    private LocalTime hora;

    private boolean disponivel;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    @JsonBackReference(value = "medico-agendaSlots")
    private Medico medico;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    @JsonBackReference(value = "consulta-agendaSlot")
    private Consulta consulta; // vincula o slot a uma consulta quando agendada
}
