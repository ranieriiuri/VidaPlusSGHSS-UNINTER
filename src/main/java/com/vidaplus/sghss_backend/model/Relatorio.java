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

    @Column(name = "gerado_por_id")
    private Long geradoPorId;

    @Column(name = "gerado_por_email")
    private String geradoPorEmail;

    @Column(name = "gerado_por_perfil")
    private String geradoPorPerfil;

}
