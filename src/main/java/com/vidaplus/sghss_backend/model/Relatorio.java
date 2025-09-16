package com.vidaplus.sghss_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "relatorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; // ex: "Relatório completo de consultas - Setembro"

    @Lob
    private String conteudoJson; // armazenar o relatório como JSON (ou XML/CSV se preferir)

    private LocalDateTime dataGeracao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario geradoPor; // apenas ADMIN pode gerar
}
