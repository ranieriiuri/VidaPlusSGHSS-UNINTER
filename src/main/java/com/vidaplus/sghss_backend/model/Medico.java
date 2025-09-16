package com.vidaplus.sghss_backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AgendaMedicaSlot> agendaSlots;

    // Relacionamento 1:1 com Usuario
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    @JsonBackReference
    private Usuario usuario;

    // Relacionamento 1:N com Consulta
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Consulta> consultas;

    @OneToMany(mappedBy = "medico")
    @JsonManagedReference
    private List<Prontuario> prontuarios;
}