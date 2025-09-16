package com.vidaplus.sghss_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    private String endereco;
    private String telefone;

    // Relacionamento 1:1 com Usuario
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    @JsonBackReference
    private Usuario usuario;

    // Relacionamento 1:N com Consulta
    @OneToMany(mappedBy = "paciente")
    @JsonManagedReference
    private List<Consulta> consultas;

    // Relacionamento 1:N com Prontuario
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Prontuario> prontuarios = new ArrayList<>();

    // Relacionamento 1:N com Notificacao
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Notificacao> notificacoes = new ArrayList<>();

    @Column(name = "teleconsulta_info")
    private String teleconsultaInfo;
}
