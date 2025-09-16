package com.vidaplus.sghss_backend.model;

import com.vidaplus.sghss_backend.model.enums.TipoNotificacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensagem;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    private LocalDateTime dataCriacao;

    private boolean lida;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}
