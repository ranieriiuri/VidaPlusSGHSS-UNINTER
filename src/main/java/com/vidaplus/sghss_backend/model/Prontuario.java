package com.vidaplus.sghss_backend.model;
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

    // Relacionamento 1:1 com Paciente
    @OneToOne
    @JoinColumn(name = "paciente_id", unique = true, nullable = false)
    private Paciente paciente;
}