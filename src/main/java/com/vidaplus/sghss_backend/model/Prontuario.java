package com.vidaplus.sghss_backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prontuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String registros;

    @Column(columnDefinition = "TEXT")
    private String prescricoes;

    // Relacionamento N:1 com Paciente
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonBackReference
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    @JsonBackReference
    private Medico medico;
}