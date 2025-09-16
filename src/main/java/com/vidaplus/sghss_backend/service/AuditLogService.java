package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.AuditLog;
import com.vidaplus.sghss_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void registrarAcao(Long usuarioId, String usuarioNome, String perfil,
                              String acao, String entidade, Long entidadeId, String detalhes) {
        AuditLog log = AuditLog.builder()
                .usuarioId(usuarioId)
                .usuarioNome(usuarioNome)
                .perfil(perfil)
                .acao(acao)
                .entidade(entidade)
                .entidadeId(entidadeId)
                .detalhes(detalhes)
                .dataHora(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }
}
