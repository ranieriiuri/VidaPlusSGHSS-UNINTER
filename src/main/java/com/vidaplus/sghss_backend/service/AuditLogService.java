package com.vidaplus.sghss_backend.service;

import com.vidaplus.sghss_backend.model.AuditLog;
import com.vidaplus.sghss_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<AuditLog> listarTodosLogs() {
        return auditLogRepository.findAll();
    }

    public List<AuditLog> listarLogsPorUsuario(Long usuarioId) {
        return auditLogRepository.findByUsuarioId(usuarioId);
    }

    public List<AuditLog> listarLogsPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return auditLogRepository.findByDataHoraBetween(inicio, fim);
    }
}
