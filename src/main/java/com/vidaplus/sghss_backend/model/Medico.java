package com.vidaplus.sghss_backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String crm;

    @Column(nullable = false)
    private String especialidade;

    private String agenda;

    // Relacionamento 1:1 com Usuario
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    @JsonBackReference
    private Usuario usuario;

    // Relacionamento 1:N com Consulta
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas;
}