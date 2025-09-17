package com.vidaplus.sghss_backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vidaplus.sghss_backend.model.enums.StatusConsulta;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsulta status;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonBackReference(value = "paciente-consultas")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    @JsonBackReference(value = "medico-consultas")
    private Medico medico;

    @OneToOne(mappedBy = "consulta")
    @JsonManagedReference(value = "consulta-agendaSlot")
    private AgendaMedicaSlot agendaSlot;
}