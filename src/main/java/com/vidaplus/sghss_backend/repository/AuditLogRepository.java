package com.vidaplus.sghss_backend.repository;

import com.vidaplus.sghss_backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsuarioId(Long usuarioId);
    List<AuditLog> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}
