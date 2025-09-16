package com.vidaplus.sghss_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String cnpj;

    private String endereco;

    private String telefone;

    private String email;

    // Futuramente pode ter relação com usuários, pacientes, etc.
}
