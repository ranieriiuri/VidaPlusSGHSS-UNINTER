package com.vidaplus.sghss_backend.dto;

public record NotificacaoRequest(
        String mensagem,
        String tipo // deve corresponder ao enum TipoNotificacao
) {}
