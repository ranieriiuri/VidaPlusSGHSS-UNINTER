package com.vidaplus.sghss_backend.dto;

import com.vidaplus.sghss_backend.model.Notificacao;
import com.vidaplus.sghss_backend.model.enums.TipoNotificacao;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class NotificacaoDTO {

    private Long id;
    private String mensagem;
    private String tipo;
    private LocalDateTime dataCriacao;
    private boolean lida;
    private Long pacienteId;

    /**
     * Converte entidade Notificacao para DTO
     */
    public static NotificacaoDTO from(Notificacao notificacao) {
        return NotificacaoDTO.builder()
                .id(notificacao.getId())
                .mensagem(notificacao.getMensagem())
                .tipo(notificacao.getTipo() != null ? notificacao.getTipo().name() : null)
                .dataCriacao(notificacao.getDataCriacao())
                .lida(notificacao.isLida())
                .pacienteId(notificacao.getPaciente() != null ? notificacao.getPaciente().getId() : null)
                .build();
    }
}
