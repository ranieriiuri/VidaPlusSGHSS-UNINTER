package com.vidaplus.sghss_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private String usuarioNome;

    private String perfil;

    private String acao; // ex: "CADASTRAR_PACIENTE", "GERAR_RELATORIO"

    private String entidade; // ex: "Paciente", "Relatorio"

    private Long entidadeId; // ID da entidade afetada, se houver

    private LocalDateTime dataHora;

    private String detalhes; // JSON ou texto com detalhes da alteração
}
